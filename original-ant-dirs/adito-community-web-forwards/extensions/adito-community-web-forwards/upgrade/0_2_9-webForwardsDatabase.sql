ALTER TABLE WEBFORWARD DROP COLUMN PARENT_RESOURCE_PERMISSION;
ALTER TABLE REPLACEMENTS DROP COLUMN USERNAME;
ALTER TABLE WEBFORWARD ADD COLUMN REALM_ID INTEGER DEFAULT 1;
        
INSERT INTO replacements (site_pattern, mime_type, sequence, match_pattern, replace_pattern) 
	VALUES ('','text/html', 36, '(\s*document\.location\s*=\s*[''\"]?)([^''\"]*)([''\"]?)','$1%2$3');
INSERT INTO replacements (site_pattern, mime_type, sequence, match_pattern, replace_pattern) 
	VALUES ('','text/html', 40, '(\s*document\.location\.href\s*=\s*[''\"]?)([^''\"]*)([''\"]?)', '$1%2$3');	
INSERT INTO replacements (site_pattern, mime_type, sequence, match_pattern, replace_pattern) 
	VALUES ('','text/html', 41, '(<meta.*content\s*=\s*[''\"]?[^''\"]*url\s*=\s*)([^'';\"]*)([;''\"])', '$1%2$3');
		
	
UPDATE replacements SET replace_pattern = '$1''/replacementProxyEngine?sslx_launchId=^T&sslex_url='' + escape($2).replace(/\\\\+/g, ''\%2C'').replace(/\\\\"/g,''\%22'').replace(/\\\\''/g, ''\%27'')' WHERE match_pattern = '(function[\s]*NavigateTo.*this\.location[\s]*=[\s]*)([^;]*)';	