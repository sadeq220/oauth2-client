package bmi.ir.ssoclient.userInfo.model;

import java.util.List;

public class Content {
    private List<Integer> inventories;
    private List<Integer> goods;
    private List<Integer> organizations;

    public List<Integer> getInventories() {
        return inventories;
    }

    public void setInventories(List<Integer> inventories) {
        this.inventories = inventories;
    }

    public List<Integer> getGoods() {
        return goods;
    }

    public void setGoods(List<Integer> goods) {
        this.goods = goods;
    }

    public List<Integer> getOrganizations() {
        return organizations;
    }

    public void setOrganizations(List<Integer> organizations) {
        this.organizations = organizations;
    }
}
