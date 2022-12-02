# 重构题

## 标题备注

代码重构

## 题目内容

在业务场景中，经常会出现很复杂的if else嵌套，假设我们的业务需要支持所有国家的名字与简写的转换，以目前的写法，会有上百个if else。

- 请在目前代码的基础上，试着优化写法，使得此段代码更好维护。
- 请以注释的形式写明重构优化的理由

## 代码片段

```Java

public class CountryNameConverter {
  // 请按你的实际需求修改参数
  public String convertCountryName(String fullName) {
        if ("china".equalsIgnoreCase(fullName)) {
            return "CN";
        } else if ("america".equalsIgnoreCase(fullName)) {
            return "US";
        } else if ("japan".equalsIgnoreCase(fullName)) {
            return "JP";
        } else if ("england".equalsIgnoreCase(fullName)) {
            return "UK";
        } else if ("france".equalsIgnoreCase(fullName)) {
            return "FR";
        } else if ("germany".equalsIgnoreCase(fullName)) {
            return "DE";
        } else {
            throw new RuntimeException("unknown country");
        }
    }
​
}
```
