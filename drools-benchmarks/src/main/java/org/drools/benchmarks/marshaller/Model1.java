package org.drools.benchmarks.marshaller;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Model1 {

    private List<Model2> model2List;

    public Model1() {}

    public Model1(List<Model2> model2List) {
        super();
        this.model2List = model2List;
    }

    public List<Model2> getModel2List() {
        return model2List;
    }

    public void setModel2List(List<Model2> model2List) {
        this.model2List = model2List;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((model2List == null) ? 0 : model2List.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Model1 other = (Model1) obj;
        if (model2List == null) {
            if (other.model2List != null)
                return false;
        } else if (!model2List.equals(other.model2List))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Model1 [model2List=" + model2List + "]";
    }

}
