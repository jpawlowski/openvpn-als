package com.adito.networkplaces.store.sftp;

import com.adito.networkplaces.NetworkPlacePlugin;
import com.adito.networkplaces.NetworkPlaceVFSProvider;
import com.adito.vfs.DefaultVFSProvider;
import com.adito.vfs.VFSProvider;

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
