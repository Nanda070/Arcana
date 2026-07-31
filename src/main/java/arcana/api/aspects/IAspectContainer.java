package arcana.api.aspects;

public interface IAspectContainer {
    AspectList getAspects();

    void setAspects(AspectList aspects);

    boolean doesContainerAccept(Aspect tag);

    int addToContainer(Aspect tag, int amount);

    boolean takeFromContainer(Aspect tag, int amount);

    boolean doesContainerContainAmount(Aspect tag, int amount);

    int containerContains(Aspect tag);
}
