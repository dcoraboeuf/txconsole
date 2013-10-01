package net.txconsole.service.support;

import lombok.Data;

@Data
public class TxFileSourceResult<T> {

    private final String version;
    private final T data;

}
