INSERT INTO replacements (site_pattern, mime_type, match_pattern, replace_pattern) 
	VALUES ('','text/html','(\s*window\.open\s*\(\s*[''\"]+)([^''\"]*)([''\"]+)','$1%2$3');
	
UPDATE replacements SET match_pattern = '(\s*document\.location\s*=\s*[''\"]+)([^''\"]*)([''\"]+)' WHERE match_pattern = '(\s*document\.location\s*=\s*[''\"]?)([^''\"]*)([''\"]?)';
UPDATE replacements SET match_pattern = '(\s*document\.location\.href\s*=\s*[''\"]+)([^''\"]*)([''\"]+)' WHERE match_pattern = '(\s*document\.location\.href\s*=\s*[''\"]?)([^''\"]*)([''\"]?)';
