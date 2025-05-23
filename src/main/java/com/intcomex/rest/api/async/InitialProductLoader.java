package com.intcomex.rest.api.async;

import com.intcomex.rest.api.config.AppProperties;
import com.intcomex.rest.api.dto.ProductCreateRequest;
import com.intcomex.rest.api.service.contract.CategoryService;
import com.intcomex.rest.api.service.contract.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Carga inicial de productos directamente en base de datos, por lotes, usando EntityManager.
 * Esta clase no usa la cola asincrónica.
 */
@Component
@Order(2)
@RequiredArgsConstructor
@Slf4j
public class InitialProductLoader implements ApplicationRunner {

    private final CategoryService categoryService;
    private final ProductService productService;
    private final AppProperties appProperties;
    private final BlockingQueue<List<ProductCreateRequest>> productBatchQueue;

    private final Random random = new Random();

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (productService.countAllProducts() > 0) {
            log.info("Productos ya están inicializados.");
            return;
        }

        List<Long> categoriesIds = categoryService.getAllCategoryIds();
        if (categoriesIds.isEmpty()) {
            log.warn("No hay categorías disponibles. No se insertarán productos.");
            return;
        }

        AppProperties.InitialProductLoad config = appProperties.getInitialProductLoad();
        if (config == null) {
            log.warn("Falta la sección 'initial-product-load' en application.yml.");
            return;
        }

        int total = config.getTotal();
        int batchSize = config.getBatchSize();

        if (total == 0) {
            log.warn("El valor del 'total' es cero, no se cargar productos");
            return;
        }
        if (total < 0 || batchSize < 0) {
            log.warn("Los valores de 'total' ({}) y 'batchSize' ({}) deben ser mayores a cero.", total, batchSize);
            return;
        }

        int adjustedBatchSize = batchSize == 0 ? total : Math.min(batchSize, total);
        int batches = (int) Math.ceil((double) total / adjustedBatchSize);

        log.info("Generando {} productos en {} lotes de hasta {} productos...", total, batches, adjustedBatchSize);

        for (int batch = 0; batch < batches; batch++) {
            int currentBatchSize = Math.min(adjustedBatchSize, total - (batch * adjustedBatchSize));

            List<ProductCreateRequest> productsNews = getProductList(categoriesIds, currentBatchSize);
            boolean enqueued = productBatchQueue.offer(productsNews);
            if (enqueued) {
                log.info("Lote {}/{} encolado ({} productos)", batch + 1, batches, currentBatchSize);
            } else {
                log.error("Cola llena al cargar el batch {}/{}. Considera reintentar o abortar el proceso.", batch + 1, batches);
                // Aquí podrías hacer un retry simple si lo necesitas
            }
            log.info("Lote {}/{} insertado con {} productos", batch + 1, batches, currentBatchSize);
        }
        log.info("Carga inicial de {} productos finalizada exitosamente.", total);
    }

    private List<ProductCreateRequest> getProductList(List<Long> categoriesIds, int totalProd) {
        // Captura las opciones de nombres de producto para evitar acceder varias veces a appProperties (por thread safety y eficiencia)
        List<AppProperties.ProductNameOptions> productNames = appProperties.getProductNames();

        return java.util.stream.IntStream.range(0, totalProd)
                .parallel()
                .mapToObj(i -> {
                    java.util.concurrent.ThreadLocalRandom tlr = java.util.concurrent.ThreadLocalRandom.current();
                    Long idCat = categoriesIds.get(tlr.nextInt(categoriesIds.size()));
                    return createRandomProduct(idCat, productNames, tlr);
                })
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Crea un producto aleatorio con nombre realista según categoría.
     */
    private ProductCreateRequest createRandomProduct(Long idCat,
                                                     List<AppProperties.ProductNameOptions> productNames,
                                                     ThreadLocalRandom tlr) {

        ProductCreateRequest pro = new ProductCreateRequest();
        pro.setProductName(generateProductName(productNames, tlr));
        pro.setQuantityPerUnit((tlr.nextInt(10) + 1) + " cajas de " + (tlr.nextInt(50) + 1) + " unidades");
        pro.setUnitPrice(BigDecimal.valueOf(tlr.nextDouble(1000.0)).setScale(2, RoundingMode.HALF_UP));
        pro.setUnitsInStock(tlr.nextInt(1000));
        pro.setUnitsOnOrder(tlr.nextInt(500));
        pro.setReorderLevel(tlr.nextInt(50));
        pro.setDiscontinued(tlr.nextBoolean());
        pro.setCategoryID(idCat);
        return pro;
    }

    /**
     * Genera un nombre del producto.
     */
    private String generateProductName(List<AppProperties.ProductNameOptions> productNames, ThreadLocalRandom tlr) {
        List<String> types = null;
        List<String> models = null;
        if (productNames != null && !productNames.isEmpty()) {
            AppProperties.ProductNameOptions options = productNames.get(tlr.nextInt(productNames.size()));
            types = options.getTypes();
            models = options.getModels();
        }
        if (types == null || types.isEmpty() || models == null || models.isEmpty()) {
            log.warn("Lista de tipos/modelos vacía para categoría");
            return "Producto-" + UUID.randomUUID().toString().substring(0, 8);
        }
        String type = types.get(tlr.nextInt(types.size()));
        String model = models.get(tlr.nextInt(models.size()));
        int number = 100 + tlr.nextInt(900);
        return type + " " + model + " " + number;
    }

}
