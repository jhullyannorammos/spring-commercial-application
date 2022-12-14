package br.com.application.daos;

import java.math.BigDecimal;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import br.com.application.builders.ProductBuilder;
import br.com.application.conf.JPAConfiguration;
import br.com.application.daos.ProductDAO;
import br.com.application.loja.conf.DataSourceConfigurationTest;
import br.com.application.models.BookType;
import br.com.application.models.Product;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ProductDAO.class,JPAConfiguration.class,DataSourceConfigurationTest.class })
@ActiveProfiles("test")
public class ProductDAOTest {

	@Autowired private ProductDAO productDAO;

	@Transactional
	@Test public void shouldSumAllPricesOfEachBookPerType() {		
		
		List<Product> printedBooks = ProductBuilder.newProduct(BookType.PRINTED, BigDecimal.TEN)
				.more(4)
				.buildAll();
		printedBooks.stream().forEach(productDAO::save);

		List<Product> ebooks = ProductBuilder.newProduct(BookType.EBOOK, BigDecimal.TEN)
				.more(4)
				.buildAll();
		ebooks.stream().forEach(productDAO::save);
		BigDecimal value = productDAO.sumPricesPerType(BookType.PRINTED);
		Assert.assertEquals(new BigDecimal(50).setScale(2), value);
		
	}
}
