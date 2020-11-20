#Boxed Expression Editor

This editor provides the possibility to edit the expression related to a Decision Node, or to a Business Knowledge Model's function.

##Structure
The main component is `src/components/BoxedExpressionEditor/BoxedExpressionEditor.tsx`.
It represents the entry point for using the editor.

In the `showcase` folder, there is a tiny React application, which represent the Proof Of Value about how it is possible to integrate the `BoxedExpressionEditor` component inside another existing application.

##Scripts
In the main project (where the components actually live), it is possible to execute, from the root folder, the following scripts (`yarn` is recommended):
```sh
# Collect and build dependencies
yarn

# Remove 'dist' folder (such script is automatically called when the build is executed)
yarn prebuild

# Build a production-ready artifact to be deployed
yarn build

# Execute all tests
yarn test

# Trigger static code analysis
yarn lint

# Trigger type checking
yarn type-check

# Perform all the three checks above (tests, lint and type checking)
yarn quality-checks
```

In the showcase project, only two scripts are available:
```sh
# Start a local server to see the 'BoxedExpressionEditor' in action
yarn start
# Compiles a production ready showcase application
yarn build
```

##Implementation details
```sh
Work in Progress
```