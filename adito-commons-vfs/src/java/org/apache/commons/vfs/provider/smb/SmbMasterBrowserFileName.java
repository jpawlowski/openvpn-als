package org.apache.commons.vfs.provider.smb;

import org.apache.commons.vfs.provider.GenericFileName;
import org.apache.commons.vfs.FileType;

public class SmbMasterBrowserFileName
    extends GenericFileName {


  public SmbMasterBrowserFileName() {
    super("smb", "", 139, 139, "", "", "", FileType.FOLDER);


  }


  public String getURI() {
    return "smb://";
  }
}
