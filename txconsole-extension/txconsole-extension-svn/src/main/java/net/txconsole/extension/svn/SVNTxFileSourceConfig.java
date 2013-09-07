package net.txconsole.extension.svn;

import lombok.Data;

@Data
public class SVNTxFileSourceConfig {

    private final String url;
    private final String user;
    private final String password;

}
