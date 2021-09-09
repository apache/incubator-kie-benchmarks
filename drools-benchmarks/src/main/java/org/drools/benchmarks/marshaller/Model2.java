package org.drools.benchmarks.marshaller;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Model2 {

    private Model3 model3;

    public Model2() {}

    public Model2(Model3 model3) {
        super();
        this.model3 = model3;
    }

    public Model3 getModel3() {
        return model3;
    }

    public void setModel3(Model3 model3) {
        this.model3 = model3;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((model3 == null) ? 0 : model3.hashCode());
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
        Model2 other = (Model2) obj;
        if (model3 == null) {
            if (other.model3 != null)
                return false;
        } else if (!model3.equals(other.model3))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Model2 [model3=" + model3 + "]";
    }

}
