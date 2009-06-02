ALTER TABLE network_places ADD COLUMN scheme varchar DEFAULT '' NOT NULL;
ALTER TABLE network_places ALTER COLUMN uri RENAME TO path;
UPDATE network_places SET scheme = 'smb' WHERE path LIKE 'smb:%';
UPDATE network_places SET scheme = 'cifs' WHERE path LIKE 'cifs:%';
UPDATE network_places SET scheme = 'ftp' WHERE path LIKE 'ftp:%';
UPDATE network_places SET scheme = 'file' WHERE path LIKE 'file:%';
UPDATE network_places SET scheme = 'sftp' WHERE path LIKE 'sftp:%';
UPDATE network_places SET scheme = 'file' WHERE path NOT LIKE 'sftp:%' AND path NOT LIKE 'smb:%' AND path NOT LIKE 'ftp:%' AND path NOT LIKE 'file:%' AND path NOT LIKE 'cifs:%';