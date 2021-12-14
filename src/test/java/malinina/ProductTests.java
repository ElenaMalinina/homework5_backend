package malinina;

import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import malinina.dao.CategoriesMapper;
import malinina.dao.ProductsMapper;
import malinina.model.Products;
import malinina.utils.DbUtils;
import malinina.utils.RetrofitUtils;
import malinina.dto.Category;
import malinina.dto.Product;
import malinina.enums.CategoryType;
import malinina.service.CategoryService;
import malinina.service.ProductService;
import okhttp3.ResponseBody;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNull;

@Slf4j
public class ProductTests {
    Integer productId;
    static Retrofit client;
    static ProductService productService;
    static CategoryService categoryService;

    static ProductsMapper productsMapper;
    static CategoriesMapper categoriesMapper;
    Faker faker = new Faker();
    Product product;

    @BeforeAll
    static void beforeAll() {
        client = RetrofitUtils.getRetrofit();
        productService = client.create(ProductService.class);
        categoryService = client.create(CategoryService.class);
        productsMapper = DbUtils.getProductsMapper();
        categoriesMapper = DbUtils.getCategoriesMapper();
    }
    @BeforeEach
    void setUp() throws IOException {
        product = new Product()
                .withTitle(faker.beer().name())
                .withPrice((int) ((Math.random()+1)*100))
                .withCategoryTitle(CategoryType.ELECTRONIC.getTitle());
        Response<Product> response = productService.createProduct(product).execute();
        productId = Objects.requireNonNull(response.body()).getId();
    }
    @Test
    void getProductTest() throws IOException {
        Response<ArrayList<Product>> response = productService.getProducts().execute();
        log.info(Objects.requireNonNull(response.body()).toString());
        assertThat(response.body().size(), CoreMatchers.not(0));
    }

    @Test
    void getProductId() throws IOException {
        Response<Product> response = productService.getProduct(productId).execute();
        log.info(Objects.requireNonNull(response.body()).toString());
        assertThat(response.body().getTitle(), equalTo(product.getTitle()));
        assertThat(response.body().getPrice(), equalTo(product.getPrice()));
        assertThat(response.body().getCategoryTitle(), equalTo(product.getCategoryTitle()));
        Products product = productsMapper.selectByPrimaryKey(productId.longValue());
        assertThat(response.body().getTitle(), equalTo(product.getTitle()));
        assertThat(response.body().getPrice(), equalTo(product.getPrice()));
    }
    @Test
    void postProductTest() throws IOException {
        Integer countProductBefore = DbUtils.countProducts(productsMapper);
        Response<Product> response = productService.createProduct(product).execute();
        Integer countProductAfter = DbUtils.countProducts(productsMapper);

        assertThat(countProductAfter, equalTo(countProductBefore+1));
        assertThat(response.body().getTitle(), equalTo(product.getTitle()));
        assertThat(response.body().getPrice(), equalTo(product.getPrice()));
        assertThat(response.body().getCategoryTitle(), equalTo(product.getCategoryTitle()));
        productId = response.body().getId();
    }
    @Test
    void getCategoryByIDTest() throws IOException {
        Integer id = CategoryType.ELECTRONIC.getId();

        Response<Category> response = categoryService
                .getCategory(id)
                .execute();
        log.info(Objects.requireNonNull(response.body()).toString());
        assertThat(response.body().getTitle(), equalTo(CategoryType.ELECTRONIC.getTitle()));
        assertThat(response.body().getId(), equalTo(id));
    }
    @Test
    void updateProductTest() throws IOException {
        Product newProduct = new Product()
                .withId(productId)
                .withCategoryTitle(CategoryType.ELECTRONIC.getTitle())
                .withPrice((int) (Math.random() * 10000000 + 1))
                .withTitle(faker.food().ingredient());
        Response<Product> response = productService.updateProduct(newProduct).execute();
        log.info(Objects.requireNonNull(response.body()).toString());
        assertThat(response.body().getId(), equalTo(productId));
        assertThat(response.body().getPrice(), equalTo(newProduct.getPrice()));
        assertThat(response.body().getTitle(), equalTo(newProduct.getTitle()));
        assertThat(response.body().getCategoryTitle(), equalTo(newProduct.getCategoryTitle()));
    }

    @Test
    void deleteProductTest() throws IOException {
        Response<ResponseBody> response = productService.deleteProduct(productId).execute();
        productId = null;
        assertNull(response.errorBody());
    }
}
