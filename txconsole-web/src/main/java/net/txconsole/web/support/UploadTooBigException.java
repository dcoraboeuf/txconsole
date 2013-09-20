package net.txconsole.web.support;

import net.txconsole.core.InputException;

public class UploadTooBigException extends InputException {
    public UploadTooBigException(long fileSizeMaxInK) {
        super(fileSizeMaxInK);
    }
}
