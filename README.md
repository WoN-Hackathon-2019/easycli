# EasyCLI

EasyCLI is a simple utility project that helps to parse commands in linux shell style in an
annotation based way.

### Build
```
mvn clean install
```

### Usage
##### Maven
Add the dependency to your pom.xml:
```xml
<dependency>
    <groupId>at.apf.easycli</groupId>
    <artifactId>easy-cli</artifactId>
    <version>0.4</version>
</dependency>
```

##### Java
Hack the engine
```java
CliEngine engine = new EasyEngine();

// Register a commands 
engine.register(new Object() {
    
    @Command("/add")
    int add(int a, int b) {
        return a + b;
    }
    
    @Command("/sort")
    int[] sort(@Flag('d') boolean desc, int[] arr) {
        if (desc)
            return this.sortDesc(arr);
        else
            return this.sortAsc(arr);
    }
    
    @Command("/send")
    void send(String receiver, @Optional String msg) {
        if (msg == null) {
            msg = "Hello";
        }
        this.sendMessage(receiver, msg);
    }
    
    @Command("/foo")
    int foo(@DefaultValue("9") int bar) {
        return bar;
    }
    
    @Command("/sendUser")
    void sendUser(String msg, @Meta User user) {
        this.sendMessage(user.getAddress(), msg);
    }
});

// Parse some command string and execute the registered implementation
int result = engine.parse("/add 2 3");            // 5
int[] sorted = engine.parse("/sort -d 7 9 2 78"); // 78 9 7 2
engine.parse("/send alice \"Hi alice\"");         // Sends 'Hi alice' to alice
engine.parse("/send alice");                      // Sends 'Hello' to alice
engine.parse("/foo 1");                           // 1
engine.parse("/foo");                             // 9
engine.parse("/sendUser \"Hi Bob\"", 
        new User("Bob"));                         // Sends 'Hi Bob' to Bob
```

### Supported Datatypes
 - char
 - int
 - long
 - float
 - double
 - boolean
 - enum
 - String
 - Array (of one of the types above)
 
### Hints
 - @DefaultValue annotation accepts only a string but gets parsed to the needed
   type. So just insert the needed value in string representation.
 - @DefaultValue "extends" @Optional. So you don't need to use both of them
   together.
 - If the argument can't be parsed to the needed type, an exception is thrown.
 - @Flag can only be annotated to boolean parameters and don't work together
   with @Optional or @DefaultValue.
 - If a parameter is not annotated with the @Optional or @DefaultValue
   annotation and the argument in the command string is missing, an exception
   is thrown.
 - The order of the arguments in the command have to be the same as in the
   defined implementation. Instead @Flag parameters can be arbitrary anywhere
   in the command.
 - Flags can be set in the command with either "-a -b -c" or in one part
   ("-abc"). If a flag is set multiple times it does not affect anything. If
   the alternative is set inside a @Flag annotation, it can be used in the
   command with double minus (e.g. "--somelongername").
 - Flags are not case-independet.
 - @DefaultValue does not work for arrays.
 - An array have to be at the end of the parameter definition and it is only
   at maximum one array allowed (else the parser does not know where one would
   end and the next one start).
 - @Optional and @DefaultValue parameters have to be at the end of the
   parameter definition. After one of those annotations was set, all upcoming
   parameters have as well to be annotated with one of them.
 - @Optional annotated parameters will be parsed to null if the argument is not
   set (also for arrays).
 - @Meta parameters can be of any type.
 - There is no limit of  @Meta parameters. Just add them in the parse call as
   separated arguments.
 - @Meta parameters can be combined with @Optional and @DefaultValue.
 - @Usage can be used for commands and flag parameters

### Changelog
##### v0.2
 - Ignoring multiple spaces in the input command and handle them as a single
   one
 - Introduced @Meta annotation to pass outstanding objects to the commands
   implementation.

##### v0.3
 - enum support
 - char support
 
##### v0.4
 - Introduced @Usage annotation and CliEngine.listCommands() and
   CliEngine.usage(command) to describe registered commands.
 
### TODOs
 - allow default values for array
 - Grouped Optionals
 

