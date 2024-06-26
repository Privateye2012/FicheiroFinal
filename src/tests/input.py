def greet():
    print("Hello, World!")
    if True:
        print("This is inside an if statement.")
    print("This is back in the greet function.")

def add(a, b):
    result = a + b
    if result > 10:
        print("The result is greater than 10.")
    else:
        print("The result is 10 or less.")
    return result

class MyClass:
    def method1(self):
        print("This is method1.")
        if True:
            print("Method1 has an if statement.")

    def method2(self):
        print("This is method2.")
