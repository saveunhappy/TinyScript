# 修改题-题目

## 标题备注

修改题

## 题目内容

阅读getCondition方法，找出其中的错误点并修复

- 要求不能使用第三方包，只能用JDK中的方法
- 要求对修复之后的方法进行测试，并打印结果

## 代码片段

```Java
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
​
public class SQLConstructor {
    
    public static void main(String[] args) {
        System.out.println("阅读getCondition方法，找出其中的错误点并修复");
        System.out.println("要求不能使用第三方包，只能用JDK中的方法");
        System.out.println("要求对修复之后的方法进行测试，并打印结果");
    }
​
    /**
     * 构造查询SQL语句
     * @param searchParams
     * @param values
     * @return 返回SQL
     */
    private static String getCondition(Map<String, Object> searchParams, List<Object> values) {
​
        StringBuilder sb = new StringBuilder();
​
        for (String s : searchParams.keySet()) {
            if ("startTime".equals(s)) {
                if (String.valueOf(searchParams.get("startTime")).contains(":")) {
                    throw new RuntimeException("请使用时间进行查询！");
                } else {
                    sb.append("startTime >= ? AND ");
                    values.add(paseDateFromLongStr(searchParams.get(s).toString()));
                }
            } else if (s.equals("DIC_TYPE")) {
                sb.append(" DIC_TYPE IN (?,?,?) AND ");
                String[] split = String.valueOf(searchParams.get(s)).split(",");
                if (split.length == 3) {
                    values.addAll(Arrays.asList(split));
                } else if (split.length < 3) {
                    // TODO 处理非3的情况
                }
            } else if ("ITEM_ID".equals(s)) {
                sb.append("ITEM_ID =? AND ");
​
            } else if ("ORG_ID".equals(s) || "COMPANY_ID".equals(s)) {
                sb.append(s + " =? AND ");
                values.add(searchParams.get(s));
            }
​
        }
        if (!searchParams.keySet().contains("DIC_CODE")) {
            sb.append(" DIC_CODE IN ('JOB_DUTY','GROUP_JOB_DUTY','USER_POST') AND ");
        }
        return sb.toString();
​
    }
​
    public static Date paseDateFromLongStr(String dateStr) {
        Date date = null;
        try {
            Long timestamp = Long.parseLong(dateStr);
            date = new Timestamp(timestamp);
        } catch (Exception e) {
            throw new RuntimeException(
                    "请检查日期格式(需使用时间戳格式的时间)");
        }
        return date;
    }
}
```
