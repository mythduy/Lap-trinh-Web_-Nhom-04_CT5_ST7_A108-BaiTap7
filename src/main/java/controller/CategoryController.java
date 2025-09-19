package controller;


import entity.Category;
import service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("admin/categories")
public class CategoryController {

	@Autowired
	private CategoryService categoryService;

	// Thư mục lưu file
	// Lấy đúng đường dẫn static trong target khi app đang chạy
	// Lưu thư mục uploads/category/ ngay cạnh pom.xml
	private static final String UPLOAD_DIR = System.getProperty("user.dir") + File.separator + "uploads"
			+ File.separator + "category" + File.separator;

	// ================= LIST =================
	@GetMapping("")
	public String list(ModelMap model) {
		List<Category> list = categoryService.getAllCategories();
		model.addAttribute("categories", list);
		return "admin/categories/list";
	}

	// ================= ADD =================
	@GetMapping("add")
	public String addForm(ModelMap model) {
		model.addAttribute("category", new Category());
		return "admin/categories/add";
	}

	@PostMapping("add")
	public ModelAndView addSubmit(@ModelAttribute("category") Category category,
			@RequestParam("imageFile") MultipartFile file, ModelMap model) throws IOException {

		if (!file.isEmpty()) {
			String fileName = UUID.randomUUID() + "_" + StringUtils.cleanPath(file.getOriginalFilename());

			File uploadDir = new File(UPLOAD_DIR);
			if (!uploadDir.exists())
				uploadDir.mkdirs();

			file.transferTo(new File(UPLOAD_DIR + fileName));

			category.setImages(fileName); // Lưu tên file vào DB
		}

		categoryService.saveCategory(category);
		model.addAttribute("message", "Category is added successfully!");
		return new ModelAndView("redirect:/admin/categories", model);
	}

	// ================= EDIT =================
	@GetMapping("edit/{id}")
	public String editForm(ModelMap model, @PathVariable("id") Integer id) {
		Category category = categoryService.getCategoryById(id);
		if (category == null) {
			model.addAttribute("message", "Category not found!");
			return "redirect:/admin/categories";
		}
		model.addAttribute("category", category);
		return "admin/categories/edit";
	}

	@PostMapping("edit")
	public ModelAndView editSubmit(@ModelAttribute("category") Category category,
			@RequestParam("imageFile") MultipartFile file, ModelMap model) throws IOException {

		if (!file.isEmpty()) {
			String fileName = UUID.randomUUID() + "_" + StringUtils.cleanPath(file.getOriginalFilename());

			File uploadDir = new File(UPLOAD_DIR);
			if (!uploadDir.exists())
				uploadDir.mkdirs();

			file.transferTo(new File(UPLOAD_DIR + fileName));

			category.setImages(fileName);
		} else {
			Category oldCategory = categoryService.getCategoryById(category.getId());
			category.setImages(oldCategory.getImages());
		}

		categoryService.saveCategory(category);
		model.addAttribute("message", "Category is updated successfully!");
		return new ModelAndView("redirect:/admin/categories", model);
	}

	@GetMapping("delete/{id}")
	public ModelAndView delete(ModelMap model, @PathVariable("id") Integer id) {
		categoryService.deleteCategory(id);
		model.addAttribute("message", "Category is deleted!");
		return new ModelAndView("redirect:/admin/categories", model);
	}

	@GetMapping("/search")
	@ResponseBody
	public List<Category> searchCategories(@RequestParam("name") String name) {
		return categoryService.searchByName(name);
	}

}