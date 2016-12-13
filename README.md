# Markdown To Spanned
=====================

Conversion of Markdown to Android Styled Text (Spanned Text) that can be used in Android Views that support them (TextView, EditText, Button, etc).

Supports almost\* all markdown tags from [John Gruber's Markdown Spec](http://daringfireball.net/projects/markdown/syntax) and more\*\*.

\* (atm) expect for images, codeblocks, and horizontal ruler

\*\* strikeout and newlines in a paragraph content treated as real line breaks

``` java
// Convert the passed Markdown String into styled Android CharSequence
textView.setText(Markdown.fromMarkdown("This is **markdown**!"));
```



## Features
===========
 * Use ´Markdown.fromMarkdown()´ just as you would use ´Html.fromHtml()´ to set styled texts on your views.

 ** TODO insert pic of example **

## Planned Features
===================

 * Load content of images from urls, assets, drawables, etc.
 * Handling of horizontal ruler
 * Configuration of style classes
 * Proper newline handling in code blocks




## Supported Markdown
=====================

** TODO add all the examples **


## Usage
========

 * TextView
 * EditText
 * Button
 * Images
    * url
    * drawable


## Download
===========

Add jitpack to you repositories

```groovy
repositories {
  ...
  maven { url 'https://jitpack.io' }
}
```

Add this to your build.gradle

```groovy
compile 'com.github.Stocard:markdown-to-spanned:v0.2.0'
```


## License
=========

     Copyright 2016 Stocard GmbH

     Licensed under the Apache License, Version 2.0 (the "License");
     you may not use this file except in compliance with the License.
     You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

     Unless required by applicable law or agreed to in writing, software
     distributed under the License is distributed on an "AS IS" BASIS,
     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     See the License for the specific language governing permissions and
     limitations under the License.
