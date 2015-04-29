package spring.mvc.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import object.dao.jdbc.IGeoDocumentDao;
import object.model.GeoDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	@Autowired
	private IGeoDocumentDao geoDocumentDao;

	@RequestMapping("/")
	public ModelAndView handleRequest() throws Exception {
		List<GeoDocument> list = geoDocumentDao.getAllH();
		ModelAndView model = new ModelAndView("GeoList");
		model.addObject("GeoList", list);
		return model;
	}
	
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public ModelAndView newGeo() {
		ModelAndView model = new ModelAndView("GeoForm");
		model.addObject("geo", new GeoDocument());
		return model;		
	}
	
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView editGeoDocument(HttpServletRequest request) {
		int userId = Integer.parseInt(request.getParameter("id"));
		GeoDocument g = new GeoDocument();
		ModelAndView model = new ModelAndView("GeoForm");
		model.addObject("geo", g);
		return model;		
	}
	
	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	public ModelAndView deleteGeoDocument(HttpServletRequest request) {
		//int doc_Id = Integer.parseInt(request.getParameter("id"));
		GeoDocument g = new GeoDocument();
		geoDocumentDao.delete(g);
		return new ModelAndView("redirect:/");		
	}
	
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public ModelAndView saveGeoDocument(@ModelAttribute GeoDocument g) {
		geoDocumentDao.saveH(g);
		return new ModelAndView("redirect:/");
	}

	/**
	 * Handles requests for the application home page.
	 */

	@RequestMapping(value="/")
	public ModelAndView home() {
		List<GeoDocument> list = geoDocumentDao.getAllH();
		ModelAndView model = new ModelAndView("home");
		model.addObject("List", list);
		return model;
	}

	
}
