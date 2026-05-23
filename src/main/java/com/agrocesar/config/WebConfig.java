package main.java.com.agrocesar.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${app.imagenes.ruta}")
    private String rutaImagenes;

    @Value("${app.imagenes.url}")
    private String urlImagenes;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(urlImagenes + "**")
                .addResourceLocations("file:" + rutaImagenes);
                
    }
}
