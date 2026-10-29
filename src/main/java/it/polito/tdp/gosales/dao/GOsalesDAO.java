package it.polito.tdp.gosales.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.gosales.model.Arco;
import it.polito.tdp.gosales.model.DailySale;
import it.polito.tdp.gosales.model.Products;
import it.polito.tdp.gosales.model.Retailers;

public class GOsalesDAO {
	
	public void creaArchi(List<Arco> archi, String brand, int anno, Map<Integer, Products> productsIdMap) {
		
		String sql = "SELECT p1.Product_number AS prod1, p2.Product_number AS prod2, COUNT(DISTINCT s1.Retailer_code) AS peso "
				+ "FROM "
				+ "(SELECT distinct g.* "
				+ "FROM go_products g "
				+ "WHERE g.Product_brand = (?) "
				+ "GROUP BY g.Product_number) p1, "
				+ "(SELECT distinct g.* "
				+ "FROM go_products g "
				+ "WHERE g.Product_brand = (?) "
				+ "GROUP BY g.Product_number) p2, "
				+ "go_daily_sales s1, "
				+ "go_daily_sales s2 "
				+ "WHERE p1.Product_number < p2.Product_number "
				+ "AND s1.Product_number = p1.Product_number "
				+ "AND s2.Product_number = p2.Product_number "
				+ "AND s1.Retailer_code = s2.Retailer_code "
				+ "AND year(s1.Date) = ? "
				+ "AND s1.Date = s2.Date "
				+ "GROUP BY p1.Product_number, p2.Product_number";
		Connection conn = DBConnect.getConnection();
		try {
		PreparedStatement st = conn.prepareStatement(sql);
		 st.setString(1, brand);
		 st.setString(2, brand);
		 st.setInt(3, anno);
		ResultSet res = st.executeQuery();
		while (res.next()) {
			Products p1 = productsIdMap.get(res.getInt("prod1"));
			Products p2 = productsIdMap.get(res.getInt("prod2"));
		Arco a = new Arco(p1, p2, res.getInt("peso"));
		archi.add(a);
		
		}
		conn.close();
		} catch (SQLException e) {
		e.printStackTrace();
		}
		
		
	}
	
	public void creaVertici(Map<Integer, Products> productsIdMap, String brand) {
		String sql = "SELECT distinct g.* "
				+ "FROM go_products g "
				+ "WHERE g.Product_brand = (?) "
				+ "GROUP BY g.Product_number";
		Connection conn = DBConnect.getConnection();
		try {
		PreparedStatement st = conn.prepareStatement(sql);
		 st.setString(1, brand);
		ResultSet res = st.executeQuery();
		while (res.next()) {
		Products p = new Products(res.getInt("Product_number"), 
				res.getString("Product_line"), 
				res.getString("Product_type"), 
				res.getString("Product"), 
				res.getString("Product_brand"), 
				res.getString("Product_color"),
				res.getDouble("Unit_cost"), 
				res.getDouble("Unit_price"));
		productsIdMap.put(res.getInt("Product_number"), p);
		}
		conn.close();
		} catch (SQLException e) {
		e.printStackTrace();
		}

	}
	
	public List<Integer> listaAnni() {
		String query = "SELECT YEAR(g.Date) as anno "
		+ "FROM go_daily_sales g "
		+ "GROUP BY YEAR(g.Date)";
		List<Integer> result = new ArrayList<Integer>();
		try {
		Connection conn = DBConnect.getConnection();
		PreparedStatement st = conn.prepareStatement(query);
		ResultSet rs = st.executeQuery();
		while (rs.next()) {
		result.add(rs.getInt("anno"));
		}
		conn.close();
		return result;
		} catch (SQLException e) {
		e.printStackTrace();
		System.out.println("Errore connessione al database");
		throw new RuntimeException("Error Connection Database");
		}
	}
	
	public List<String> listaBrand() {
		String query = "SELECT distinct g.Product_brand AS brand "
				+ "FROM go_products g "
				+ "ORDER BY g.Product_brand";
		List<String> result = new ArrayList<String>();
		try {
		Connection conn = DBConnect.getConnection();
		PreparedStatement st = conn.prepareStatement(query);
		ResultSet rs = st.executeQuery();
		while (rs.next()) {
		result.add(rs.getString("brand"));
		}
		conn.close();
		return result;
		} catch (SQLException e) {
		e.printStackTrace();
		System.out.println("Errore connessione al database");
		throw new RuntimeException("Error Connection Database");
		}
	}
	
	/**
	 * Metodo per leggere la lista di tutti i rivenditori dal database
	 * @return
	 */

	public List<Retailers> getAllRetailers(){
		String query = "SELECT * from go_retailers";
		List<Retailers> result = new ArrayList<Retailers>();
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(query);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(new Retailers(rs.getInt("Retailer_code"), 
						rs.getString("Retailer_name"),
						rs.getString("Type"), 
						rs.getString("Country")));
			}
			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		
	}
	
	
	/**
	 * Metodo per leggere la lista di tutti i prodotti dal database
	 * @return
	 */
	public List<Products> getAllProducts(){
		String query = "SELECT * from go_products";
		List<Products> result = new ArrayList<Products>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(query);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(new Products(rs.getInt("Product_number"), 
						rs.getString("Product_line"), 
						rs.getString("Product_type"), 
						rs.getString("Product"), 
						rs.getString("Product_brand"), 
						rs.getString("Product_color"),
						rs.getDouble("Unit_cost"), 
						rs.getDouble("Unit_price")));
			}
			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		
	}

	
	/**
	 * Metodo per leggere la lista di tutte le vendite nel database
	 * @return
	 */
	public List<DailySale> getAllSales(){
		String query = "SELECT * from go_daily_sales";
		List<DailySale> result = new ArrayList<DailySale>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(query);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(new DailySale(rs.getInt("retailer_code"),
				rs.getInt("product_number"),
				rs.getInt("order_method_code"),
				rs.getTimestamp("date").toLocalDateTime().toLocalDate(),
				rs.getInt("quantity"),
				rs.getDouble("unit_price"),
				rs.getDouble("unit_sale_price")  ));
			}
			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	
}
