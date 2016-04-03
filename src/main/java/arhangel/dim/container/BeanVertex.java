package arhangel.dim.container;

/**
 * Вершина графа, которая содержит бин
 */
public class BeanVertex {
    private Bean bean;
//    private boolean isVisited;

    public BeanVertex(Bean bean) {
        this.bean = bean;
//        this.isVisited = false;
    }

    public Bean getBean() {
        return bean;
    }

    public void setBean(Bean bean) {
        this.bean = bean;
    }

//    public void visit(){
//        this.isVisited = true;
//    }
//    public void notVisit(){
//        this.isVisited = true;
//    }

}
