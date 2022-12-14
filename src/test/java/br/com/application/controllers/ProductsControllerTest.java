package br.com.application.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import javax.servlet.Filter;
import javax.transaction.Transactional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.annotation.Timed;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import br.com.application.builders.ProductBuilder;
import br.com.application.conf.AppWebConfiguration;
import br.com.application.conf.JPAConfiguration;
import br.com.application.conf.SecurityConfiguration;
import br.com.application.daos.ProductDAO;
import br.com.application.loja.conf.DataSourceConfigurationTest;
import br.com.application.models.BookType;
import br.com.application.models.Price;
import br.com.application.models.Product;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { 
		AppWebConfiguration.class,
		JPAConfiguration.class, 
		SecurityConfiguration.class,
		DataSourceConfigurationTest.class })
@ActiveProfiles("test")
public class ProductsControllerTest {

	@Autowired private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Autowired private Filter springSecurityFilterChain;
	@Autowired private ProductDAO productDAO;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
				.addFilters(springSecurityFilterChain).build();
	}

	@Test
	public void onlyAdminShoudAccessProductsForm()
			throws Exception {
		this.mockMvc.perform(
				get("/produtos/form").with(
						SecurityMockMvcRequestPostProcessors
								.user("comprador@gmail.com").password("123456")
								.roles("COMPRADOR"))).andExpect(
				status().is(403));
	}

	@Test
	@Transactional
	public void shouldListAllBooksInTheHome()
			throws Exception {
		productDAO.save(ProductBuilder.newProduct().buildOne());

		ResultActions action = this.mockMvc.perform(get("/produtos"));
		ResultMatcher modelAndViewMatcher = new ResultMatcher() {

			@Override
			public void match(MvcResult result) throws Exception {
				ModelAndView mv = result.getModelAndView();
				List<Product> products = (List<Product>) mv.getModel().get("products");
				Assert.assertEquals(1, products.size());
			}
		};
		action.andExpect(modelAndViewMatcher).andExpect(MockMvcResultMatchers.forwardedUrl("/WEB-INF/views/products/list.jsp"));

	}

}
