package org.drools.benchmarks.marshaller;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Model3 {

    private String name;
    private int number;

    public Model3() {}

    public Model3(String name, int number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + number;
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
        Model3 other = (Model3) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (number != other.number)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Model3 [name=" + name + ", number=" + number + "]";
    }

}
