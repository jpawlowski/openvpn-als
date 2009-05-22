package com.ovpnals.networkplaces.store.sftp;

import com.ovpnals.networkplaces.NetworkPlacePlugin;
import com.ovpnals.networkplaces.NetworkPlaceVFSProvider;
import com.ovpnals.vfs.DefaultVFSProvider;
import com.ovpnals.vfs.VFSProvider;

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
