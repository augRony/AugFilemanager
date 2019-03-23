
package com.augustro.filemanager.filesystem.ssh;

import android.support.annotation.NonNull;

import net.schmizz.sshj.connection.channel.direct.Session;

import java.io.IOException;

public abstract class SshClientSessionTemplate
{
    public final String url;

    /**
     * Constructor.
     *
     * @param url SSH connection URL, in the form of <code>ssh://&lt;username&gt;:&lt;password&gt;@&lt;host&gt;:&lt;port&gt;</code> or <code>ssh://&lt;username&gt;@&lt;host&gt;:&lt;port&gt;</code>
     */
    public SshClientSessionTemplate(@NonNull String url)
    {
        this.url = url;
    }

    /**
     * Implement logic here.
     *
     * @param sshClientSession {@link Session} instance, with connection opened and authenticated
     * @param <T> Requested return type
     * @return Result of the execution of the type requested
     */
    public abstract <T> T execute(@NonNull Session sshClientSession) throws IOException;
}
