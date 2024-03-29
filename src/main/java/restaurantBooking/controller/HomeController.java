package restaurantBooking.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import restaurantBooking.dao.HomeDao;
import restaurantBooking.entity.Cart;
import restaurantBooking.entity.Menu;
import restaurantBooking.entity.Seat;
import restaurantBooking.entity.User;

@Controller
public class HomeController {
	@Autowired
	HomeDao dao;

	@RequestMapping(method = RequestMethod.GET, value = "/login")
	public String getLogin(@RequestParam(name = "error", required = false) String error,
			@RequestParam(name = "success", required = false) String success, Model model) {
		model.addAttribute("user", new User());
		model.addAttribute("error", error);
		model.addAttribute("success", success);
		return "user/login";
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/404")
	public String accessDeny() {
		return "user/404";
	}

	@RequestMapping(method = RequestMethod.GET, value = "/logout")
	public String logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			new SecurityContextLogoutHandler().logout(httpServletRequest, httpServletResponse, authentication);
		}
		return "redirect:/login";
	}

	@RequestMapping(method = RequestMethod.GET, value = "/register")
	public String getRegister(@RequestParam(name = "error", required = false) String error, Model model) {
		model.addAttribute("user", new User());
		model.addAttribute("error", error);
		return "user/register";
	}

	@RequestMapping(method = RequestMethod.POST, value = "/register")
	public String postRegister(@ModelAttribute("user") User user) {
		int status;
		status = dao.register(user);
		if (status > 0) {
			return "redirect:/login?success=success";
		} else {
			return "redirect:/register?error=failed";
		}

	}

	@RequestMapping(method = RequestMethod.GET, value = {"/", "home"})
	public String home() {
		return "user/about";
	}

	@RequestMapping(method = RequestMethod.GET, value = "/success")
	public String getSuccessOrder() {
		return "user/successOrder";
	}

	@RequestMapping(method = RequestMethod.GET, value = "/menu")
	public String showMenu(Model model) {
		List<Menu> menu = dao.getAllMenu();
		model.addAttribute("menu", menu);
		return "user/foodMenu";
	}

	@RequestMapping(method = RequestMethod.GET, value = "/food")
	public String showDetails(@RequestParam(name = "id") int id, Model model) {
		Menu menu = new Menu();
		menu = dao.findFoodById(id);
		model.addAttribute("menu", menu);
		return "user/foodDetails";
	}

	@RequestMapping(method = RequestMethod.GET, value = "/booking")
	public String bookTable(Model model, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {
		List<String> seatNames = new ArrayList<String>();
		seatNames = dao.getEmptySeats();
		System.out.println(seatNames);
		model.addAttribute("seatNames", seatNames);
		model.addAttribute("seat", new Seat());
		return "user/foodBooking";
	}

	@RequestMapping(method = RequestMethod.POST, value = "/booking")
	public String bookTable(@ModelAttribute("seat") Seat seat, Model model, Principal principal) {
		String name = principal.getName();
		int id = dao.findIdByName(name);
		seat.setUserId(id);
//		model.addAttribute("seat", seat);
		Seat existSeat = dao.existSeat(seat);

		if (existSeat != null) {
			System.out.println("Da het ghe!");
		} else {
			System.out.println("Van con ghe!");
			System.out.println(existSeat);
			dao.addSeat(seat);
			if (dao.addSeat(seat) > 0) {
				System.out.println("success");
			}
		}
		return "redirect:/";
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/buy")
	public String addCart(HttpSession session, @RequestParam("id") int id, Principal principal) {
		String username = principal.getName();
		User user = dao.findByUser(username);
		
		HashMap<Integer, Cart> cart =  (HashMap<Integer, Cart>) session.getAttribute("cart");
		if(cart == null) {
			cart = new HashMap<Integer, Cart>();
		}
		cart = dao.addCart(id, cart);
		session.setAttribute("cart", cart);
		session.setAttribute("user", user);
		session.setAttribute("totalPrice", dao.totalPrice(cart));
		return "redirect:/cart";
	}

	@RequestMapping(method = RequestMethod.GET, value = "/cart")
	public String viewCart(Model model) {
		return "user/cart";
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/placeOrder")
	public String placeOrder(HttpSession session, Principal principal, Model model) {
		HashMap<Integer, Cart> cart = (HashMap<Integer, Cart>) session.getAttribute("cart");
		ObjectMapper mapper = new ObjectMapper();
		List<Cart> items = new ArrayList<Cart>();
		double totalPrice = (double) session.getAttribute("totalPrice");
		for (Map.Entry<Integer, Cart> entry: cart.entrySet()) {
			items.add(entry.getValue());
		}
		
		try {
			String content = mapper.writeValueAsString(items);
			System.out.println(content);
			String username = principal.getName();
			User user = dao.findByUser(username);
			dao.order(user, content, totalPrice);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String message = "Order successfully!";
		model.addAttribute("message", message);
		session.removeAttribute("cart");
		session.removeAttribute("totalPrice");
		return "user/cart";
	}
}
