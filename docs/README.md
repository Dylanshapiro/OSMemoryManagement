# Design and Architecture
With *OSMM* our goal was to provide an informative visualization of well-known memory allocation algorithms by simulating the allocation of
operating system processes from different sources. To achieve this while allowing for ergonomic maintenance and extensibility, we have employed various OO design
patterns.

OSMM is designed using a traditional MVC approach as commonly seen in gui development. It's components are implemented in the
[model](/src/main/java/model),
[view](/src/main/java/view), and 
[controller](/src/main/java/controller) packages.
* [UML](#uml)
* [Sequence Digram](#sequence-diagram)
* [Instantiation](#instantiation)
* [The Model](#the-model)
* [Observables](#observables)
* [The Controller](#the-controller)
* [The View](#the-view)

## UML
![uml](https://user-images.githubusercontent.com/43762253/49850554-f0716b80-fdab-11e8-8e8b-9e4c590ca4cc.PNG)

## Sequence Diagram
![uml](https://i.imgur.com/URFWIl8.png)

## Instantiation
```java
// Driver.java

 FXMLLoader loader = new FXMLLoader(getClass()
.getResource("../xml/Root.fxml"));
```
The applications UI [components](/src/main/resources/xml) are defined via FXML,
JavaFX's XML based language. We specify a root gui component to be loaded. This root component
contains includes for other FXML files required to compose itself. Each FXML file declares
a "controller", a Java class that will handle any logic to be done on the file's nodes. Typically,
JavaFX itself would instantiate the UI components defined in the root file, all the files it includes,
and the Java "controller" classes declared within them.

We opt out out of the last point by providing our own Factory class.

```java
// Driver.java
loader.setControllerFactory(new ComponentFactory(ctrl));
```
Now JavaFx will delegate the instantiation of each UI component's controller class to our Factory instead of 
instantiating everything in it's typical more opaque manner. We use this control to wire dependencies through
annotations.

[ComponentFactory](/src/main/java/driver/ComponentFactory.java) provides the @VIEW and @CTRL annotations.

* *@VIEW* enables our *Controller* class to easily get a reference to the root gui component should it need it,
or to leave out when it does not. This will help to remove clutter in situations where a GUI is not relevant,
such as if *Controller* is operating as part of a simulation intended only to serve as remote source
for some client running elsewhere.

* *@CTRL* enables UI components to easily get a reference to our *Controller* Class should they need it.
Buttons, fields, and other user input handling components can utilize this to access functionality exposed
by *Controller*, while more static components can ignore it.

These annotations allow us to eliminate complexities that would otherwise be necessary in both the
construction of Controller as well as every UI component. 

## The Model

In our quest to provide an insightful visualization we modelled the behaviour of an operating system when as processes come and go,
placed into memory accordingly.

#### *Process*
The [Process](/src/main/java/model/process/Process.java) 
class simply models an operating system process. *Process*'s all have a procId, startTime, state, name, and baseAddress.
```java
// Process.java

  public Process(String name, int procId, long startTime, Long size) {
        this.name = name;
        this.procId = procId;
        this.startTime = startTime;
        this.state = state.READY;
        this.size = size;
        this.baseAddress = Optional.empty();
}
```

Another important thing to notice is *Process*'s baseAddress variable. Upon construction, it is not yet known where the *Process*
will be allocated. We take advantage of Java 8's *Optional* type to make explicit the fact that any given *Process* may 
not have a baseAddress at any point in time. This makes things much clearer for Classes where working *Process* objects 
directly is necessary. Classes like *MemoryManager* (explained in depth later) rely on whether or not a baseAddress is present
to determine whether a processes has been allocated or not. Process conveniently offers clients the ability to check
this by simply wrapping *Optional*'s isPresent().

#### *ProcessSource*
In the [model](/src/main/java/model) package you will find
the definition for OSMM's *ProcessSource* interface. 

```java
// ProcessSource.java

public interface ProcessSource{

    public List<Process> getAll();

    public void kill(int pid) throws IOException;

    public Process generateProcess();

    public int getId();

    public void sim();
}
```
Any live operating system is going to have processes starting and stopping for
various reasons. The implementers of *ProcessSource* seek to model that
behaviour at it's core.

```java
// SimSource.java

    @Override
    public void sim() {
        int rand = getRandomIntBetweenRange(0, 2);
        if (rand > 0) {
            notifyNewProcess(generateProcess());
        } else {
            rand = getRandomIntBetweenRange(0, processList.size());
            notifyKillProcess(processList.get(rand));
            processList.remove(rand);
        }
    }
```
```java
// LocalSource.java

    @Override
    public void sim() {
        List<Process> updatedList = this.getAll();

        // if updatedList does not contain a process that the old list 
        // had, notify controller that the process was deleted
        this.localProcs.stream()
                .filter(p -> !(containsProc(updatedList, p)))
                .forEach(p -> notifyKillProcess(p));

        // if updatedList contains a process that the old list did not,
        // notify controller that a process was added
        updatedList.stream()
                .filter(p -> !(containsProc(this.localProcs, p)))
                .forEach(p -> notifyNewProcess(p));

        this.localProcs = updatedList;
    }
```
A call made to sim() on a *ProcessSource* advances the state of the simulation forward. With each
call processes may stop, start, or remain unchanged.
[SimSource](/src/main/java/model/process/SimSource.java)
 creates processes through the use of an algorithm that generates processes with
 pseudo-random properties. [LocalSource](/src/main/java/model/process/LocalSource.java)
 generates processes my mimicking the behaviour of the host system, using an algorithm
 that looks for changes in the the state of the system whenever a call to sim() is made.
 Any Class that can operate within this paradigm is fit to 
 implement *ProcessSource*.
 
#### *MemoryManager*

Simulating the creation of processes is not very useful in and of itself. If you could simulate the allocation of those processes
however, that could be interesting. [MemoryManager](/src/main/java/model/MemoryManager.java)
takes care of facilitating that simulation for *OSMM*. 

*MemoryManager* is implemented as a Singleton.
```java
// MemoryManager.java

    public static MemoryManager getInstance(){
    
        if(memoryManager==null){
            long memSize = defaultMemSize();
            Algo algo = new FirstFitAlgo(memSize);
            memoryManager=new MemoryManager(memSize,algo);
        }
        return memoryManager;
}
```
The Class's getInstance() method creates the sole instance upon the first call to it. This instance is stored
and returned. References to this same instance are returned on any subsequent calls to getInstance().

You may have noticed in the method about that *MemoryManager*'s constructor is passed an an algorithm.
By default it will be constructed with a class implemented by OSMM that simulates the FirstFit memory
allocation algorithm. *MemoryManager*'s functionality does not end there though, we have implemented a full
suite of algorithm's and provide clients with the ability to swap them out seamlessly via the Strategy design
pattern.

```java
// MemoryManager.java

    public void setAlgo(Algo memoryAlgo) {
        this.memoryAlgo = memoryAlgo;
        memoryAlgo.setMemSize(memSize);
        this.memoryAlgo.setRepresentation(processes);
}
```
*MemoryManager* provides other methods required to facilitate the simulation 
as well.
```java
// MemoryManager.java

    public List<Process> getAllProc(){...}
    public boolean allocate(Process p){...}
    public boolean deallocate(Process p){...}
    public Process getProcess(int procID) {...}
    public long getMemSize() {...}
    public void reset() {...}
    public void clearProc() {...}
```
##### Allocating a Process
The process *MemoryManager* uses to allocate any single unique process is relatively straight forward.

1. A client passes in a process which has not yet been assigned a baseAddress via *MemoryManager*'s 
allocate() method.
2. *MemoryManager* performs checks to see if there is enough room left to fit a *Process* the size 
of the one it was passed.
3. *MemoryManager* calls allocPs() on the *Algo* that is is currently set to use.
4. The *Algo* will try and find a baseAddress where it can put the *Process*. If it finds one, it will
call setBaseAddress() on the *Process*, assigning to it the address that was found.
5. *MemoryManager* receives the allocation address (if one was found), and stores it within an
internal list.

#### *MemoryEvent*
For *OSMM* to function *MemoryManager* cannot be some opaque structure simulating allocation 
just for the sake of doing so. It must provide the outside world with some means to monitor 
what is going on inside of it.

*MemoryEvent* does exactly that. 

```java
// MemoryManager.java

  public enum Op{
        ADD,
        KILL,
        RESET,
        ERROR,
    }

    public class MemoryEvent{

        private List<Process> processes;
        public long memSize;
        private Process lastChanged;
        private Op type;
        private Optional<String> statusMessage;

        public MemoryEvent(List<Process> processes,Process process,
                           long memSize, Op type) {
            this.processes = processes;
            this.memSize = memSize;
            this.lastChanged=process;
            this.type = type;
            this.statusMessage = Optional.empty();
}
```
Any method call on *MemoryManager* that is deemed by *OSMM* to be an "event" will produce
a corresponding *MemoryEvent*. A *MemoryEvent* is a convenient summary of what last occurred
within the *MemoryManager*, perfect for exporting to those curious in the outside world. Once
clients have obtained a *MemoryEvent* they can use it's various getters to draw conclusions
about what is going on inside of *MemoryManager*. 

## Observables

Throughout the description of *ProcessSource* and *MemoryManager* you may have noticed that
most of the methods meant to construct objects for the outside world are of void return type.  

So if these methods are not returning objects back out of the class, what is the mechanism for communication between these classes? 
 
When sim() is called on *ProcessSource* where do generate processes go?  
When an event occurs within *MemoryManager* where does the generated *MemoryEvent* go?  

In short, *OSMM* implements the
*Observer* design pattern through 2 custom *Observable*s, [ProcessSourceObservable](/src/main/java/model/process/ProcessSourceObservable.java),
and [MemoryObservable](/src/main/java/model/MemoryObservable.java).
We have also implemented the corresponding *Observer*s, [ProcessSourceObserver](/src/main/java/model/process/ProcessSourceObserver.java),
and [MemoryObserver](/src/main/java/model/MemoryObserver.java).
I am not going to go deep into the implementation of these classes. Just know that if you are familiar with Java's
*java.util.Observable* and *java.util.Observer*, *OSMM*'s implementations of the pattern will function mostly how you would intuitively expect.

The main difference being that our *Observable* implementations notify their *Observers* on different methods depending
on the state of the sim.

Implementers of *ProcessSourceObservable* will notify their *Observers* when a new process has been created through the 
notifyNewProcess() method, and will call notifyKillProcess() when a process should be deallocated.
```java
// SimSource.java

@Override
    public void sim() {
        int rand = getRandomIntBetweenRange(0, 2);
        if (rand > 0) {
            notifyNewProcess(generateProcess());
        } else {
            rand = getRandomIntBetweenRange(0, processList.size());
            notifyKillProcess(processList.get(rand));
            processList.remove(rand);
        }
    }
```
Registered *Observables* will receive a notification by overriding killProcess() and newProcess().
```java
    @Override
    public void killProcess(Process p) {
        System.out.println("You have been notified to deallocate process: " + p.getName());
    }

    @Override
    public void newProcess(Process p) {
          System.out.println("You have received a new process " + p.getName());
}
```
Implementers of *MemoryObservable* will notify their *Observers* when a *MemoryEvent* has occurred through
the methods notifyObservers() and notifyObserversError().
```java
// MemoryManager.java

    private void notifyObservers(Process p, Op type, String message){
        final MemoryEvent event = new MemoryEvent(processes, p, memSize, type);
        event.setStatusMessage(message);
        this.notifyObservers(event);
    }
```
Registered *Observables* will receive notifications by overriding update() and error().
```java
// Controller.java

    @Override
    public void update(MemoryObservable obs, MemoryEvent memEvent) {
        Platform.runLater(() -> {
            this.view.updateDisplay(memEvent);// send update to view
        });
}

    @Override
    public void error(String err) {
        System.out.println(err);
}
```

## The Controller
Our [Controller](/src/main/java/controller/Controller.java) class
perhaps bears the largest burden out of *OSMM*'s classes. Classes like *ProcessSource* and *MemoryManager* might provide
the lion's share of *OSMM*'s functionality by implementing by handling many low level details, but *Controller* takes care of
composing that functionality in a way that makes *OSMM* a joy to use. 

*Controller* is one of the first classes to be instantiated when the app is run. Its constructor sets up a few things.
```java

    public Controller(MemoryManager manager, ProcessSourceObservable... sourceList) {

        this.handle = Optional.empty();
        this.execService = Executors.newScheduledThreadPool(1, r -> {
            Thread thread = Executors.defaultThreadFactory().newThread(r);
            thread.setDaemon(true);
            return thread;
        });

        this.manager = manager;
        this.sourceList = Arrays.asList(sourceList);

        this.source = (ProcessSource) this.sourceList.get(0);
        ((ProcessSourceObservable) this.source).addObserver(this);
}
```
* Assigns Optional.empty() to an Optional \<ScheduledFuture> handle
    * will be used to store a handle to a future that facilitates running *OSMM*'s simulation.
    * if empty, then a simulation isn't running 
* Creates a new ScheduledExecutorService
    * Uses a single thread for now but more could be used if it was warranted.
    * The pool's thread is set to be a daemon to handle automatic shutdown if the user thread stops running.
* Stores a reference to the *MemoryManager* singleton
* Stores a list of available *ProcessSource*'s
* Stores a reference to a *ProcessSource* that will function as the current source for the simulation
* Registers itself as a *ProcessSourceObserver* on the current *ProcessSource*
    * note that by this point *Controller* will have already been registered as a *MemoryObserver*
    on *MemoryManager* within *OSMM*'s *Driver* Class.

At this point the *OSMM*'s simulation is wired up and ready to go. Looking at *Controller*'s startSim() method
you will see what what happens when the "Start Sim" button is pressed.

```java
// Controller.java

    public void startSim() {

        ScheduledFuture<?> handle = this.execService.scheduleWithFixedDelay(() -> {

            this.source.sim();

        }, 0, 300, TimeUnit.MILLISECONDS);
        this.handle = Optional.of(handle);
    }
```
We call scheduleWithFixedDelay() on *Controller*'s *ScheduledExecutorService*, this allows us to provide 
execService with a task and a delay value. Once this task is done, execService will wait for the amount of 
time we set before running the task again. This repeats until the task is cancelled.

So we are scheduling sim() on *Controller*'s current *ProcessSource*, within the *ScheduledExecutorService*'s thread pool, nearly every 300ms until the task is cancelled.
 
 A reference to the task
is stored in *Controller*'s handle variable, we can later call cancel on this, as is done when "Stop Sim" is pressed
and stopSim() is called. This stops the simulation.
```java
// Controller.java

public void stopSim() {
        this.handle.ifPresent(handle -> {
            handle.cancel(false);
            this.handle = Optional.empty();
        });
    }
```
Apart from running the simulation, *Controller* exposes a variety of useful functions that gui components can take advantage of.
```java
// Controller.java

 public List<ProcessSource> getSourceList() {...}
 public List<Algo> getAlgoList() {...}
 public long getMemSize() {...}
 public void resetSim() {...}
 public void addProc() {...}
 public void setSource(String id){...}
 public void setSource(int id){...}
 public void setAlgo(Algo a) {...}
```
##### Running the simulation
The loop that typically occurs when the simulation running is relatively straight forward.
1. "Start Sim" is pressed
2.  The *View* calls startSim() on *Controller*

    The following will occur nearly every 300ms until "Stop Sim" is pressed and stopSim() is called.
    1. *Controller*'s *ScheduledExecutorService* calls sim() on the current *ProcessSource*
    2. Any potentially blocking io that is required to generate a process is done within the *ScheduledExecutorService*'s pool.
    3. The current *ProcessSource* will notify *Controller* with a *Process* via *Controller*'s
    newProcess() or killProcess() methods after and work has been done and if the simulation's state has changed.
    4. If *Controller* receive's a callback via killProcess() or newProcess() it will schedule a task with it's *ScheduledExecutorService* 
    to call the corresponding method on
    *MemoryManager*, allocate() or deallocate() to simulate the allocation/deallocation of the process.
    
        ```java
        // Controller.java
 
        @Override
        public void killProcess(Process p) {
            CompletableFuture.runAsync(() -> {
                Platform.runLater(() -> manager.deallocate(p.getProcId()));
            }, execService);
        }
    
        @Override
        public void newProcess(Process p) {
            CompletableFuture.runAsync(() ->
                            Platform.runLater(() -> this.manager.allocate(p))
                    , execService);
        }
        ```
    5. *MemoryManager* will notify *Controller* with a *MemoryEvent* via *Controller*'s
           update()  method.
    6. The *MemoryEvent* is passed back into JavaFX's world via Platform.runLater(), where 
    references to it can be distributed to our *Root* ui component and its children. From there any changes that need to occur
    within the gui will occur on the main(JavaFX/gui) thread.
        ```java
       // Controller.java
     
        @Override
        public void update(MemoryObservable obs, MemoryEvent memEvent) {
            Platform.runLater(() -> {
                this.view.updateDisplay(memEvent);// send update to view
            });
        }
        ```

3. The *View* calls stopSim() on *Controller*, cancelling the current task within *ScheduledExecutorService* and stopping the sim. 
```java
// Controller.java

    public void stopSim() {
        this.handle.ifPresent(handle -> {
            handle.cancel(false);
            this.handle = Optional.empty();
        });
    }
```
## The View

The [View](/src/main/java/view) implementation of *OSMM*'s MVC is heavily influenced by JavaFX, but we still utilize all
  the tools out our disposal to ensure a clear separation of concerns.
  
Every gui component consists of an FXML file and a corresponding Java "controller" class. Where other JavaFX applications often have
domain logic in close proximity to the methods which are interacting directly with their gui, *OSMM*'s gui "controller" classes 
contain no domain logic. Instead the "controller" classes of the *View*'s gui components serve as a thin layer
between the domain logic of our *Controller* class and the elements displayed on the gui.

The result is all gui components within *OSMM* are composed in such a manner.
* [FXML](/src/main/resources/xml): Handle's declaring what JavaFX provided component we will use (Buttons,Panes,TextFields,ect..), and allow for their instantiation
in *Driver* via *ComponentFactory*
* [CSS](/src/main/resources/css): Handles styling components and setting layout properties
* [Java "controller" Class](/src/main/java/view/component): Contains the logic that manipulates the component

### Accessing sim functionality via the GUI

#### Calling *Controller*'s methods
UI components can easily take advantage of *OSMM*'s *@CTRL* annotation to access the functionality exposed by controller.
```java
// ActionButtons.java

@CTRL
private Controller ctrl;
```
*ComponentFactory* will now instantiate *ActionButtons* with a reference to *Controller* assigned to ctrl.

#### Setting Listeners
We utilize FXML's onAction property within the FXML files of all our components.
This allows use to declare a function to be called in the corresponding Java "controller" class that will
be called when an Action is performed on the component.
```xml
// ActionButtons.fxml

    <Button id="addButton" mnemonicParsing="false" text="Add Process"
            onAction="#addProc" stylesheets="@../css/button.css" />
```

#### Binding to FXML properties 
Sometimes a node or property declared within a controller's FXML file needs to be be accessed directly for an arbitrary reason.
We use JavaFX's *@FXML* annotation to do this wherever necessary.
```xml
// MiniTerm.fxml

<TextArea xmlns="http://javafx.com/javafx"
          xmlns:fx="http://javafx.com/fxml"
          fx:controller="view.component.MiniTerm"
          fx:id="miniTerm" />
```


```java
// MiniTerm.java

 @FXML
 private TextArea miniTerm;

 private void printStatus(MemoryEvent event){
        String op = event.getOpType().name();

        event.getStatusMessage().ifPresent(status ->  {
            miniTerm.appendText(String.format("%-7s: %s%n", op, status));
        });
}
```
This allows direct manipulation of the node or property. In this case we use it to access a TextField where we output various information
every time a new *MemoryEvent* is received.

#### Receiving and Reacting to *MemoryEvents*
UI components can optionally declare an update() method if they wish to be alerted whenever
a new *MemoryEvent* occurs. *Root* will be passed all new *MemoryEvents* and will pass references
to the *MemoryEvent* to all child gui components that that have defined update().
