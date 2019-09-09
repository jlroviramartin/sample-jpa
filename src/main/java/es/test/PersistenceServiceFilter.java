package es.test;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

@WebFilter
public class PersistenceServiceFilter implements Filter {

    private static final Logger LOG = Logger.getLogger(PersistenceServiceFilter.class.getName());

    public PersistenceServiceFilter() {
    }

    /**
     * Init method for this filter
     *
     * @param filterConfig
     *            Configuration.
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOG.log(Level.INFO, "Iniciando el filtro para el EntityManager del JPA");

        //String nameOfPU = filterConfig.getInitParameter("PERSISTENT_PU");
        //if (null == nameOfPU) {
        //    throw new ServletException("Filtro de persistencia no inicializado correctamente. El nombre del PU es nulo. Falta el parámetro PERSISTENT_PU al filtro");
        //}

        try {
            PersistenceService.init(PersistenceService.NAME_OF_PU);
        } catch (Throwable th) {
            LOG.log(Level.SEVERE, th.getMessage());
            throw new ServletException("Filtro de persistencia no inicializado correctamente.", th);
        }

        LOG.log(Level.INFO, "Iniciado correctamente el filtro para el EntityManager del JPA");
    }

    /**
     * Destroy method for this filter
     */
    @Override
    public void destroy() {
        LOG.log(Level.INFO, "Destruyendo el filtro para el EntityManager del JPA");
        PersistenceService.destroy(true);
        LOG.log(Level.INFO, "Destruido el filtro para el EntityManager del JPA");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain fc) throws IOException, ServletException {
        try (PersistenceService ps = PersistenceService.getInstance()) {
            fc.doFilter(request, response);
        }
    }
}
