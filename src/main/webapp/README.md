# Bare Aurelia Skeleton App
This guide shows you how to setup a basic skeleton for creating aurelia apps. 

## Prerequisites
1. Ensure that [NodeJS](http://nodejs.org/) is installed.
2. Ensure that [jspm](http://jspm.io/) is installed. If you need to install it, use the following command:

```npm install -g jspm```

jspm uses github to fetch dependencies and there is a limit on anonymous fetches. You probably need to setup 
jspm to use some credentials. This is done in one of the two following ways.
 
1. create a ~/.netrc with the following content
```
machine github.com
login your-login
password your-password

machine api.github.com
login your-login
password your-password
```
If you use this don't forget to change rights to 600.
```shell
chmod 660 ~/.netrc
```

2. Configure credential in jspm using
```
:q
jspm registry config github
```

## Creating the skeleton App
1. Create a project directory, e.g. skeleton-app

```shell
mkdir skeleton-app
```

2. Initialize jspm from inside the skeleton-app directory you just created

```shell
jspm init
```

3. Add aurelia dependencies

```shell
jspm install aurelia-framework
jspm install aurelia-bootstrapper
```

4. Create an index.html

```html
<html>
<head>
    <title>Hello from Aurelia</title>
</head>
<body aurelia-app>
    <script src="jspm_packages/system.js"></script>
    <script src="config.js"></script>
    <script>
        System.import("aurelia-bootstrapper");        
    </script>
</body>
</html>
```