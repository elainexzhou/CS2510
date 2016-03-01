//Assignment 7
//Zhou, Xiaolu
//elaine07

import tester.Tester;
// To represent function objects of two types
interface IFunc<T, U> {
    // applies this function object
    U apply(T item);
}

// To represent a function object returning the instructor of a course
class CourseToInstructor implements IFunc<Course, Instructor> {
    public Instructor apply(Course item) {
        return item.prof;
    }
}

// To represent sameness tests:
interface ISame<T> {
    // are the two given Ts the same?
    boolean same(T t1, T t2);
}

// To represent sameness tests for students
class SameStudent implements ISame<Student> {
    // are the two given students the same?
    // (do they have the same ID number?)
    public boolean same(Student t1, Student t2) {
        return t1.id == t2.id;
    }
}

// To represent sameness tests for courses
class SameCourse implements ISame<Course> {
    // are the two given courses the same?
    // (do they have the same name and same instructor?)
    public boolean same(Course t1, Course t2) {
        // using == checks for object sameness
        // this ensures a check that they really are the same professor
        return t1.name.equals(t2.name) && t1.prof == t2.prof;
    }
}

// To represent sameness tests for instructors
class SameInstructor implements ISame<Instructor> {
    // are the two given instructors the same?
    // (are they the same object?)
    public boolean same(Instructor t1, Instructor t2) {
        return t1 == t2;
    }
}

// To represent lists:
interface IList<T> {
    // adds an item to this list
    IList<T> append(T item);
    // does this list contain the given item using the given sameness check?
    boolean contains(T item, ISame<T> check);
    // does this list contain any of the items in the given list, using the given sameness check?
    boolean containsAny(IList<T> items, ISame<T> check);
    // how many times does the given item occur in the list, using the given sameness check?
    int count(T item, ISame<T> check);
    // maps this list to another data type with the given function object
    <U> IList<U> map(IFunc<T, U> f);
}

// To represent empty lists:
class Empty<T> implements IList<T> {
    // adds an item to this empty list
    public IList<T> append(T item) {
        return new Cons<T>(item, this);
    }
    
    // does this empty list contain the given T?
    public boolean contains(T item, ISame<T> check) {
        return false;
    }
    
    // does this empty list contain any items in the given list?
    public boolean containsAny(IList<T> items, ISame<T> check) {
        return false;
    }
    
    // how many times does the given item occur in this empty list?
    public int count(T item, ISame<T> check) {
        return 0;
    }
    
    // map this empty list with the given function object
    public <U> IList<U> map(IFunc<T, U> f) {
        return new Empty<U>();
    }
}

// To represent non-empty lists:
class Cons<T> implements IList<T> {
    T first;
    IList<T> rest;
    
    Cons(T first, IList<T> rest) {
        this.first = first;
        this.rest = rest;
    }

    // adds an item to this non-empty list
    public IList<T> append(T item) {
        return new Cons<T>(this.first, this.rest.append(item));
    }
    
    // does this nonempty list contain the given T, based on the given sameness check?
    public boolean contains(T item, ISame<T> check) {
        return check.same(item, this.first) || this.rest.contains(item, check);
    }
    
    // does this nonempty list contain any items also in the given list, based on the given
    //     sameness check?
    public boolean containsAny(IList<T> items, ISame<T> check) {
        return items.contains(this.first, check) || this.rest.containsAny(items, check);
    }
    
    // how many times does the given item occur in this list, based on the given
    //     sameness check?
    public int count(T item, ISame<T> check) {
        if (check.same(this.first, item)) {
            return 1 + this.rest.count(item, check);
        }
        else {
            return this.rest.count(item, check);
        }
    }
    
    // maps this nonempty list with the given function object
    public <U> IList<U> map(IFunc<T, U> f) {
        return new Cons<U>(f.apply(this.first), this.rest.map(f));
    }
}

// To represent a course
class Course {
    String name;
    Instructor prof;
    IList<Student> students;
    //constructor
    Course(String name, Instructor prof) {
        this.name = name;
        this.prof = prof;
        this.students = new Empty<Student>();
        this.prof.addCourse(this);
    }

    // Adds a student to this course
    // EFFECT: modifies this.students
    void addStudent(Student s) {
        this.students = this.students.append(s);
    }
}

// To represent an instructor
class Instructor {
    String name;
    IList<Course> course;
    //constructor
    Instructor(String name) {
        this.name = name;
        this.course = new Empty<Course>();
    }

    // Adds the given course to this professor's list of courses
    // EFFECT: modifies this.courses
    void addCourse(Course c) {
        this.course = this.course.append(c);
    }
    
    // Does this instructor have this student in more than one course?
    boolean dejavu(Student s) {
        return s.classesWithInstructor(this) > 1;
    }
}

// To represent a student
class Student {
    String name;
    int id;
    IList<Course> course;
    //constructor
    Student(String name, int id) {
        this.name = name;
        this.id = id;
        this.course = new Empty<Course>();
    }
    
    // Enrolls this student in the given course
    // EFFECT: modifies this.courses and the given course
    void enroll(Course c) {
        this.course = new Cons<Course>(c, this.course);
        c.addStudent(this);
    }
    
    // Is this student in any courses with the given student?
    boolean classmates(Student s) {
        return this.course.containsAny(s.course, new SameCourse());
    }
    
    // How many classes does this student have with the given instructor?
    int classesWithInstructor(Instructor prof) {
        IList<Instructor> profs = this.course.map(new CourseToInstructor());
        return profs.count(prof, new SameInstructor());
    }
}

class ExamplesRegistrar {
    // fields to remain constant throughout the testing process
    ISame<Student> sameStudent = new SameStudent();
    ISame<Course> sameCourse = new SameCourse();
    
    // fields that are used for testing and may be modified during testing
    Student a;
    Student b;
    Student c;
    Student d;
    Student e;
    Instructor f;
    Instructor g;
    Instructor h;
    Course c1;
    Course c2;
    Course c3;
    Course c4;
    Course c5;
    Course c6;
    
    //example lists to test methods in IList
    IList<Student> mt = new Empty<Student>();
    IList<Student> l1 = new Cons<Student>(this.a, new Cons<Student>(this.b, mt)); 
    IList<Student> l2 = new Cons<Student>(this.d, new Cons<Student>(this.e,
            new Cons<Student>(this.c, mt)));
    IList<Student> l3 = new Cons<Student>(this.a, new Cons<Student>(this.b,
            new Cons<Student>(this.e, mt)));
    
    // initializes the students and instructors to known values
    // this is used for testing the course creation
    // EFFECT: modifies each of the above Student/Instructor fields
    void initializeConditions() {
        this.a = new Student("Anna", 1);
        this.b = new Student("Ben", 2);
        this.c = new Student("Charlie", 3);
        this.d = new Student("Daniel", 4);
        this.e = new Student("Elaine", 5);
        this.f = new Instructor("Fred");
        this.g = new Instructor("Grace");
        this.h = new Instructor("Haley");
    }
    
    // initializes all of the above fields to known values
    // this is used for all tests except course creation
    // EFFECT: modifies each of the above Student/Instructor/Course fields
    void initializeConditionsCourses() {
        this.initializeConditions();
        this.c1 = new Course("CS2500", this.g);
        this.c2 = new Course("CS2500", this.g);
        this.c3 = new Course("ARTG3250", this.h);
        this.c4 = new Course("ARTH1111", this.h);
        this.c5 = new Course("CS3500", this.f);
        this.c6 = new Course("CS4800", this.f);
    }
    
    // initializes all of the above fields to known values
    // additionally, enrolls students into known courses
    // EFFECT: modifies each of the above Student/Instructor/Course fields
    void initializeConditionsEnroll() {
        this.initializeConditionsCourses();
        this.a.enroll(this.c4);
        this.b.enroll(this.c5);
        this.c.enroll(this.c6);
        this.d.enroll(this.c1);
        this.d.enroll(this.c2);
        this.e.enroll(this.c6);
    }
    
    // test course creation
    void testCourse(Tester t) {
        // initialize to known conditions
        this.initializeConditions();
        
        // update the courses
        // EFFECT: modifies the professor given to each Course constructor
        this.c1 = new Course("CS2500", this.g);
        this.c2 = new Course("CS2500", this.g);
        this.c3 = new Course("ARTG3250", this.h);
        this.c4 = new Course("ARTH1111", this.h);
        this.c5 = new Course("CS3500", this.f);
        this.c6 = new Course("CS4800", this.f);
        
        // test that course's professor was updated correctly
        t.checkExpect(this.c1.prof, this.g);
        t.checkExpect(this.c2.prof, this.g);
        t.checkExpect(this.c3.prof, this.h);
        t.checkExpect(this.c4.prof, this.h);
        t.checkExpect(this.c5.prof, this.f);
        t.checkExpect(this.c6.prof, this.f);
        
        // test that the professor's courses were updated correctly
        t.checkExpect(this.g.course, new Cons<Course>(this.c1,
                new Cons<Course>(this.c2, new Empty<Course>())));
        t.checkExpect(this.h.course, new Cons<Course>(this.c3,
                new Cons<Course>(this.c4, new Empty<Course>())));
        t.checkExpect(this.f.course, new Cons<Course>(this.c5,
                new Cons<Course>(this.c6, new Empty<Course>())));
    }
    
    // test enrollment of students
    void testEnroll(Tester t) {
        // initialize to known conditions
        this.initializeConditionsCourses();
        
        // enroll students in courses
        // EFFECT: modifies the student and the course
        this.a.enroll(this.c4);
        this.b.enroll(this.c5);
        this.c.enroll(this.c6);
        this.d.enroll(this.c1);
        this.d.enroll(this.c2);
        this.e.enroll(this.c6);
        
        // test that the students' course list was updated correctly
        t.checkExpect(this.a.course, new Cons<Course>(this.c4, new Empty<Course>()));
        t.checkExpect(this.b.course, new Cons<Course>(this.c5, new Empty<Course>()));
        t.checkExpect(this.c.course, new Cons<Course>(this.c6, new Empty<Course>()));
        t.checkExpect(this.d.course, new Cons<Course>(this.c1,
                new Cons<Course>(this.c2, new Empty<Course>())));
        t.checkExpect(this.e.course, new Cons<Course>(this.c6, new Empty<Course>()));
        
        // test that the courses' student list was updated correctly
        t.checkExpect(this.c4.students, new Cons<Student>(this.a, new Empty<Student>()));
        t.checkExpect(this.c5.students, new Cons<Student>(this.b, new Empty<Student>()));
        t.checkExpect(this.c6.students, new Cons<Student>(this.c,
                new Cons<Student>(this.e, new Empty<Student>())));
        t.checkExpect(this.c1.students, new Cons<Student>(this.d, new Empty<Student>()));
        t.checkExpect(this.c2.students, new Cons<Student>(this.d, new Empty<Student>()));
        t.checkExpect(this.c3.students, new Empty<Student>());
    }
    
    // test Student.classmates():
    void testClassmates(Tester t) {
        // initialize to known conditions
        this.initializeConditionsEnroll();
        
        // test whether students are classmates
        t.checkExpect(this.a.classmates(this.b), false);
        t.checkExpect(this.c.classmates(this.d), false);
        t.checkExpect(this.c.classmates(this.e), true);
        t.checkExpect(this.e.classmates(this.c), true);
        t.checkExpect(this.e.classmates(this.b), false);
    }
    
    // test Instructor.dejavu():
    void testDejavu(Tester t) {
        // initialize to known conditions
        this.initializeConditionsEnroll();
        
        // test whether an instructor has a student in multiple classes
        t.checkExpect(this.g.dejavu(this.d), true);
        t.checkExpect(this.g.dejavu(this.e), false);
        t.checkExpect(this.h.dejavu(this.a), false);
        t.checkExpect(this.f.dejavu(this.b), false);
    }
}