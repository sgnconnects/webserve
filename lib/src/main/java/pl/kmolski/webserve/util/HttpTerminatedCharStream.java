package pl.kmolski.webserve.util;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.UnbufferedCharStream;
import org.antlr.v4.runtime.misc.Interval;

public class HttpTerminatedCharStream implements CharStream {

    private final UnbufferedCharStream wrapped;
    private final int maxSymbolCount;

    private final int[] previousSymbols = new int[2];
    private int consecutiveCrlfCount = 0;

    // TODO: add timeouts
    public HttpTerminatedCharStream(UnbufferedCharStream wrapped, int maxSymbolCount) {
        this.wrapped = wrapped;
        this.maxSymbolCount = maxSymbolCount;
    }

    @Override
    public String getText(Interval interval) {
        return this.wrapped.getText(interval);
    }

    @Override
    public void consume() {
        this.previousSymbols[0] = this.wrapped.LA(-1);
        this.previousSymbols[1] = this.wrapped.LA(1);

        if (this.previousSymbols[0] == '\r' && this.previousSymbols[1] == '\n') {
            if (++this.consecutiveCrlfCount >= 2) {
                return;
            }
        } else if (this.previousSymbols[1] != '\r') { // skip reset if ...<CR> <LF> <CR> are read
            this.consecutiveCrlfCount = 0;
        }

        this.wrapped.consume();
    }

    @Override
    public int LA(int i) {
        if (this.wrapped.index() > this.maxSymbolCount) {
            // TODO: throw bad request
            throw new IllegalArgumentException("Header too long");
        } else if (this.consecutiveCrlfCount >= 2) {
            return Token.EOF;
        }

        return this.wrapped.LA(i);
    }

    @Override
    public int mark() {
        return this.wrapped.mark();
    }

    @Override
    public void release(int marker) {
        this.wrapped.release(marker);
    }

    @Override
    public int index() {
        return this.wrapped.index() + (this.consecutiveCrlfCount >= 2 ? 1 : 0);
    }

    @Override
    public void seek(int index) {
        this.wrapped.seek(index);
    }

    @Override
    public int size() {
        return this.wrapped.size();
    }

    @Override
    public String getSourceName() {
        return this.wrapped.getSourceName();
    }
}
