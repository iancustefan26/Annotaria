package org.example.wepproject.Helpers.CsrfValidation;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;

import java.io.IOException;

// Custom ServletInputStream for cached body
public class CachedBodyServletInputStream extends ServletInputStream {
    private final byte[] cachedBody;
    private int lastIndexRetrieved = -1;
    private ReadListener readListener = null;

    public CachedBodyServletInputStream(byte[] cachedBody) {
        this.cachedBody = cachedBody;
    }

    @Override
    public boolean isFinished() {
        return (lastIndexRetrieved == cachedBody.length - 1);
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setReadListener(ReadListener readListener) {
        this.readListener = readListener;
        if (!isFinished()) {
            try {
                readListener.onDataAvailable();
            } catch (IOException e) {
                readListener.onError(e);
            }
        } else {
            try {
                readListener.onAllDataRead();
            } catch (IOException e) {
                readListener.onError(e);
            }
        }
    }

    @Override
    public int read() throws IOException {
        int i;
        if (!isFinished()) {
            i = cachedBody[lastIndexRetrieved + 1];
            lastIndexRetrieved++;
            if (isFinished() && (readListener != null)) {
                try {
                    readListener.onAllDataRead();
                } catch (IOException ex) {
                    readListener.onError(ex);
                    throw ex;
                }
            }
            return i;
        } else {
            return -1;
        }
    }
}
