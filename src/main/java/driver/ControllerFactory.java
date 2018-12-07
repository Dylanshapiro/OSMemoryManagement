package driver;

import controller.Controller;
import javafx.util.Callback;
import view.component.Root;

import java.lang.reflect.Field;
import java.util.Optional;

/* Gives all instances of classes in the component package that have the variable "ctrl"
 * declared a reference to ctrl (the Controller passed into this factory's constructor).
 * Gives the Controller passed into this factory a ref to the root gui component (Root) if it
 * has the variable "view" declared*/
public class ControllerFactory implements Callback<java.lang.Class<?>, java.lang.Object> {

    private final Controller ctrl;

    public ControllerFactory(Controller ctrl) {
        this.ctrl = ctrl;
    }

    @Override
    public Object call(Class<?> controllerType) {
        if (controllerType == Root.class) {

            try {
                // Set Ref to Controller in Root
                Object root = initWithRef(controllerType, "ctrl", this.ctrl);

                // Set Ref to Root in Controller
                setIfPresent(ctrl,"view",root);
                return root;

            } catch (NoSuchFieldException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
            throw new RuntimeException();

        } else if (controllerType.getPackage().getName().contains("component")) {
            try {
               return initWithRef(controllerType,"ctrl",ctrl);

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

    private Object initWithRef(Class<?> controllerType, String field, Object ref)
            throws IllegalAccessException, InstantiationException, NoSuchFieldException {

        Object obj = controllerType.newInstance();
        Class objClass = obj.getClass();

        Optional.ofNullable(objClass.getDeclaredField(field))
                .ifPresent(presField -> setPrivField(presField, obj, ref));

        return obj;
    }

    private void setIfPresent(Object classInstance, String field, Object val)
            throws NoSuchFieldException {
        Class classObj = classInstance.getClass();

        Optional.ofNullable(classObj.getDeclaredField(field))
                .ifPresent(presField -> setPrivField(presField,classInstance,val));
    }

    private void setPrivField(Field f, Object o, Object val) {
        try {
            f.setAccessible(true);
            f.set(o, val);
            // f.setAccessible(false);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}


