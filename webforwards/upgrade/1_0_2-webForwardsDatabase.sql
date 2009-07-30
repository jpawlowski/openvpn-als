
UPDATE webforward SET type = 3 WHERE type = 2 and id IN ( SELECT webforward_id FROM reverse_proxy_options WHERE active_dns = 1 OR host_header <> '' )