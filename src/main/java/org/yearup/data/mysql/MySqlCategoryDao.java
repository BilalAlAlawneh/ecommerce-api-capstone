package org.yearup.data.mysql;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.stereotype.Component;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao {
    private final BasicDataSource basicDataSource;

    public MySqlCategoryDao(DataSource dataSource, BasicDataSource basicDataSource)
    {
        super(dataSource);
        this.basicDataSource = basicDataSource;
    }

    @Override
    public List<Category> getAllCategories()
    {
        List<Category> categories = new ArrayList<>();

        String sql = "SELECT category_id, name, description FROM categories";
        try (Connection connection = basicDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery())
        {
            while (resultSet.next())
            {
                Category category = new Category(
                        resultSet.getInt("category_id"),
                        resultSet.getString("name"),
                        resultSet.getString("description")
                );
                categories.add(category);
            }
        }
        catch (SQLException e)
        {
            System.out.println("Error fetching categories " + e.getMessage());
        }
        // get all categories
    return categories;
    }

    @Override
    public Category getById(int categoryId)
    {
        // get category by id
        Category category = null;
        String sql = "SELECT category_id, name, description FROM categories WHERE category_id = ?";
        try(Connection connection = basicDataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){

            preparedStatement.setInt(1, categoryId);

            try(ResultSet resultSet = preparedStatement.executeQuery()){
                if(resultSet.next()){
                    category = new Category(
                            resultSet.getInt("category_id"),
                            resultSet.getString("name"),
                            resultSet.getString("description")
                    );
                }
            }
        }
        catch (SQLException e){
            System.out.println("Error fetching category by id: " + e.getMessage());
        }
        return category;
    }

    @Override
    public Category create(Category category)
    {
        // create a new category
        String sql = "INSERT INTO categories (name, description) Values (?, ?)";
        try (Connection connection = basicDataSource.getConnection();
             PreparedStatement preparedStatement =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            preparedStatement.setString(1, category.getName());
            preparedStatement.setString(2, category.getDescription());
            preparedStatement.executeUpdate();

            try (ResultSet keys = preparedStatement.getGeneratedKeys())
            {
                if (keys.next())
                {
                    category.setCategoryId(keys.getInt(1));
                }
            }
        }
        catch (SQLException e)
        {
            System.out.println("Error creating category: " + e.getMessage());
        }

        return category;
    }

    @Override
    public void update(int categoryId, Category category)
    {
        String sql = "UPDATE categories SET name = ?, description = ? WHERE category_id = ?";

        try (Connection connection = basicDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql))
        {
            preparedStatement.setString(1, category.getName());
            preparedStatement.setString(2, category.getDescription());
            preparedStatement.setInt(3, categoryId);
            preparedStatement.executeUpdate();
        }
        catch (SQLException e)
        {
            System.out.println("Error updating category: " + e.getMessage());
        }
        // update category
    }

    @Override
    public void delete(int categoryId)
    {
        String sql = "DELETE FROM categories WHERE category_id = ?";

        try (Connection connection = basicDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql))
        {
            preparedStatement.setInt(1, categoryId);
            preparedStatement.executeUpdate();
        }
        catch (SQLException e)
        {
            System.out.println("Error deleting category: " + e.getMessage());
        }
    }

    private Category mapRow(ResultSet row) throws SQLException
    {
        int categoryId = row.getInt("category_id");
        String name = row.getString("name");
        String description = row.getString("description");

        Category category = new Category()
        {{
            setCategoryId(categoryId);
            setName(name);
            setDescription(description);
        }};

        return category;
    }
    }
