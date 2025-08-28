package in.co.rays.util;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import in.co.rays.bean.BaseBean;
import in.co.rays.bean.DropdownListBean;
import in.co.rays.bean.RoleBean;
import in.co.rays.model.RoleModel;

/**
 * The {@code HTMLUtility} class provides utility methods for generating 
 * HTML <select> dropdown lists from either a {@link HashMap} or a {@link List}.
 * It is commonly used in JSP/Servlet-based applications to dynamically 
 * populate dropdown fields from data sources.
 * 
 * <p>This class supports two main input sources:</p>
 * <ul>
 *   <li>A {@code HashMap} where keys represent the option values and 
 *       values represent the display labels.</li>
 *   <li>A {@code List} of {@link DropdownListBean} objects where each 
 *       bean provides a key-value pair for the dropdown.</li>
 * </ul>
 * 
 * <p>It also contains test methods to demonstrate dropdown list generation.</p>
 * 
 * @author 
 */
public class HTMLUtility {

    /**
     * Generates an HTML <select> dropdown list from a {@link HashMap}.
     *
     * @param name        the name attribute of the select element
     * @param selectedVal the value that should be pre-selected
     * @param map         a {@link HashMap} containing option values and display text
     * @return an HTML string representing the dropdown list
     */
    public static String getList(String name, String selectedVal, HashMap<String, String> map) {

        StringBuffer sb = new StringBuffer(
                "<select style=\"width: 170px;text-align-last: center;\"; class='form-control' name='" + name + "'>");

        sb.append("\n<option selected value=''>-------------Select-------------</option>");

        Set<String> keys = map.keySet();
        String val = null;

        for (String key : keys) {
            val = map.get(key);
            if (key.trim().equals(selectedVal)) {
                sb.append("\n<option selected value='" + key + "'>" + val + "</option>");
            } else {
                sb.append("\n<option value='" + key + "'>" + val + "</option>");
            }
        }
        sb.append("\n</select>");
        return sb.toString();
    }

    /**
     * Generates an HTML <select> dropdown list from a {@link List} of beans.
     *
     * @param name        the name attribute of the select element
     * @param selectedVal the value that should be pre-selected
     * @param list        a {@link List} of {@link DropdownListBean} objects
     * @return an HTML string representing the dropdown list
     */
    public static String getList(String name, String selectedVal, List list) {

        List<DropdownListBean> dd = (List<DropdownListBean>) list;

        StringBuffer sb = new StringBuffer("<select style=\"width: 170px;text-align-last: center;\"; "
                + "class='form-control' name='" + name + "'>");

        sb.append("\n<option selected value=''>-------------Select-------------</option>");

        String key = null;
        String val = null;

        for (DropdownListBean obj : dd) {
            key = obj.getKey();
            val = obj.getValue();

            if (key.trim().equals(selectedVal)) {
                sb.append("\n<option selected value='" + key + "'>" + val + "</option>");
            } else {
                sb.append("\n<option value='" + key + "'>" + val + "</option>");
            }
        }
        sb.append("\n</select>");
        return sb.toString();
    }

    /**
     * Test method to demonstrate dropdown generation using a {@link HashMap}.
     * Prints the generated HTML to the console.
     */
    public static void testGetListByMap() {

        HashMap<String, String> map = new HashMap<>();
        map.put("male", "male");
        map.put("female", "female");

        String selectedValue = "male";
        String htmlSelectFromMap = HTMLUtility.getList("gender", selectedValue, map);

        System.out.println(htmlSelectFromMap);
    }

    /**
     * Test method to demonstrate dropdown generation using a {@link List} of roles.
     * Fetches role data from {@link RoleModel} and prints the HTML dropdown.
     *
     * @throws Exception if an error occurs while fetching role data
     */
    public static void testGetListByList() throws Exception {

        RoleModel model = new RoleModel();

        List<RoleBean> list = model.list();

        String selectedValue = null;

        String htmlSelectFromList = HTMLUtility.getList("fname", selectedValue, list);

        System.out.println(htmlSelectFromList);
    }

    /**
     * Main method to execute test methods for generating HTML dropdowns.
     *
     * @param args command-line arguments
     * @throws Exception if an error occurs during execution
     */
    public static void main(String[] args) throws Exception {

        // testGetListByMap();

        testGetListByList();

    }
}
