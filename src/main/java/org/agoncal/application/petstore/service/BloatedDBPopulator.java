package org.agoncal.application.petstore.service;

import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.sql.DataSourceDefinition;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.agoncal.application.petstore.domain.Category;
import org.agoncal.application.petstore.domain.Item;
import org.agoncal.application.petstore.domain.Product;
import org.agoncal.application.petstore.util.Loggable;

@Singleton
@Startup
@Loggable
@DataSourceDefinition(className = "org.apache.derby.jdbc.EmbeddedDataSource", name = "java:global/jdbc/applicationPetstoreDS", user = "app", password = "app", databaseName = "applicationPetstoreDB", properties = { "connectionAttributes=;create=true" })
public class BloatedDBPopulator implements BloatedDBPopulatorMXBean {

    @Inject
    private Logger log;

    @Inject
    private CatalogService catalogService;

    private static final List<String> petImages = Arrays.asList("bird1.jpg", "bird2.jpg", "cat1.jpg", "cat2.jpg", "dog1.jpg", "dog2.jpg", "dog3.jpg", "dog4.jpg", "dog5.jpg", "dog6.jpg", "fish1.jpg", "fish2.jpg", "fish3.jpg", "fish4.jpg", "lizard1.jpg", "reptile1.jpg");

    private static final List<Category> catagories = Arrays.asList(
                                                        new Category("Fish", "Any of numerous cold-blooded aquatic vertebrates characteristically having fins, gills, and a streamlined body"), 
                                                        new Category("Dogs", "A domesticated carnivorous mammal related to the foxes and wolves and raised in a wide variety of breeds"), 
                                                        new Category("Reptiles", "Any of various cold-blooded, usually egg-laying vertebrates, such as a snake, lizard, crocodile, turtle"), 
                                                        new Category("Cats", " Small carnivorous mammal domesticated since early times as a catcher of rats and mice and as a pet and existing in several distinctive breeds and varieties"), new Category("Birds", "Any of the class Aves of warm-blooded, egg-laying, feathered vertebrates with forelimbs modified to form wings"));

    @PostConstruct
    private void registerMBean() {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName name;
        try {
            name = new ObjectName(BloatedDBPopulatorMXBean.OBJECT_NAME);
            mbs.registerMBean(this, name);
        } catch (Exception e) {
            log.severe("Error while registering mx bean");
        }
    }

    private void clearDB() {
        for (Category cat : catalogService.findAllCategories()) {
            catalogService.removeCategory(cat);
        }
    }

    private String randomString() {
        return UUID.randomUUID().toString().substring(0, 29);
    }

    private Category selectRandomCategory() {
        return catagories.get((int) (Math.random() * catagories.size()));
    }

    private String selectRandomImage() {
        return petImages.get((int) (Math.random() * petImages.size()));
    }

    private void initCatalog(int numberOfEntries) {
        clearDB();

        for (int i = 0; i < numberOfEntries; i++) {
            Category catagory = selectRandomCategory();
            Product product = new Product(randomString(), randomString(), catagory);
            catagory.addProduct(product);

            // add between 1 and 10 items
            for (int k = 0; k < (int) (Math.random() * 10); k++) {
                String image = selectRandomImage();
                Item item = new Item(randomString(), 100.0f, image, product, randomString());
                product.addItem(item);
            }
        }

        for (Category catagory : catagories) {
            catalogService.createCategory(catagory);
        }
    }

    @Override
    public void bloatDB(int numberOfEntries) {
        initCatalog(numberOfEntries);
    }

    @Override
    public void emptyDB() {
        clearDB();
    }

}
