package net.openvpn.als.networkplaces.store.sftp;

import net.openvpn.als.networkplaces.NetworkPlacePlugin;
import net.openvpn.als.networkplaces.NetworkPlaceVFSProvider;
import net.openvpn.als.vfs.DefaultVFSProvider;
import net.openvpn.als.vfs.VFSProvider;

public class SFTPProvider extends DefaultVFSProvider implements NetworkPlaceVFSProvider{
    public SFTPProvider() {
        super(SFTPStore.SFTP_SCHEME,
            true,
            false,
            VFSProvider.ELEMENT_REQUIRED,
            VFSProvider.ELEMENT_NOT_REQUIRED,
            VFSProvider.ELEMENT_NOT_REQUIRED,
            VFSProvider.ELEMENT_NOT_REQUIRED,
            SFTPStore.class,
            NetworkPlacePlugin.MESSAGE_RESOURCES_KEY);
    }	

}
