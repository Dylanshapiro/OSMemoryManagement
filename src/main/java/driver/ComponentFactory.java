package driver;

import controller.Controller;
import javafx.util.Callback;
import view.component.Root;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;

/* - If the controller passed in has a field annotated with @VIEW, the field will be
 * assigned a ref to the Root ui component.
 *  - If a Class is in the component package and has a field annotated with @CTRL,
 * the field will be assigned a ref to the Controller that is passed in to this factory*/
public class ComponentFactory implements Callback<Class<?>, Object> {

    private final Controller ctrl;

    public ComponentFactory(Controller ctrl) {
        this.ctrl = ctrl;
    }

    @Override
    public Object call(Class<?> controllerType) {
        if (controllerType == Root.class) {

            try {
                // Set Ref to Controller in Root
                Object root = initWithRef(controllerType, CTRL.class, this.ctrl);

                // Set Ref to Root in Controller

                Arrays.stream(ctrl.getClass().getDeclaredFields())
                        .filter(field -> field.isAnnotationPresent(VIEW.class))
                        .findFirst()
                        .ifPresent(field -> setPrivField(field, ctrl, root));

                return root;

            } catch (NoSuchFieldException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
            throw new RuntimeException();

        } else if (controllerType.getPackage().getName().contains("component")) {
            try {
                return initWithRef(controllerType, CTRL.class, ctrl);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                return controllerType.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Object initWithRef(Class<?> controllerType, Class<CTRL> annotation, Object ref)
            throws IllegalAccessException, InstantiationException, NoSuchFieldException {

        Object obj = controllerType.newInstance();
        Class objClass = obj.getClass();

        Arrays.stream(objClass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(annotation))
                .findFirst()
                .ifPresent(field -> setPrivField(field, obj, ref));

        return obj;
    }

    private void setPrivField(Field f, Object o, Object val) {
        try {
            f.setAccessible(true);
            f.set(o, val);
            f.setAccessible(false);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface CTRL {

    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface VIEW {

    }
}


