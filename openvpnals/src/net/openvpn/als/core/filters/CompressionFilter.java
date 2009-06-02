package net.openvpn.als.core.filters;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.openvpn.als.core.CoreUtil;
import net.openvpn.als.security.Constants;
import net.openvpn.als.security.LogonControllerFactory;
import net.openvpn.als.security.User;

public class CompressionFilter implements Filter {

    private FilterConfig config = null;

    protected int compressionThreshold;

    final static Log log = LogFactory.getLog(CompressionFilter.class);

    /* (non-Javadoc)
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    public void init(FilterConfig filterConfig) {
        config = filterConfig;
        compressionThreshold = 0;
        if (filterConfig != null) {
            String str = filterConfig.getInitParameter("compressionThreshold");
            if (str != null) {
                compressionThreshold = Integer.parseInt(str);
            } else {
                compressionThreshold = 0;
            }
        }
    }

    /* (non-Javadoc)
     * @see javax.servlet.Filter#destroy()
     */
    public void destroy() {
        this.config = null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        boolean supportCompression = false;
        boolean compressionEnabled = true;
        if (request instanceof HttpServletRequest && !Boolean.FALSE.equals(request.getAttribute(Constants.REQ_ATTR_COMPRESS))) {
            try {
                User user = LogonControllerFactory.getInstance().getUser((HttpServletRequest) request);
                compressionEnabled = CoreUtil.getUsersProfilePropertyBoolean(((HttpServletRequest) request).getSession(), "webServer.compression", user);
            } catch (Exception ex) {
            }
            if (compressionEnabled) {
                Enumeration e = ((HttpServletRequest) request).getHeaders("Accept-Encoding");
                while (e.hasMoreElements()) {
                    String name = (String) e.nextElement();
                    if (name.indexOf("gzip") != -1) {
                        supportCompression = true;
                    }
                }
            }
        }
        if (supportCompression && response instanceof HttpServletResponse) {
            GZIPResponseWrapper wrappedResponse =
                new GZIPResponseWrapper((HttpServletResponse)response);
              chain.doFilter(request, wrappedResponse);
              wrappedResponse.finishResponse();
        } else {
            chain.doFilter(request, response);
        }
    }
}
