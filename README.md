# Lovecraft Library

## Description

This is an Android application which fetches the information about the all of the available Electronic Texts of H.P. Lovecraft's Works and manipulates them in certain ways, most of all to give an accesible way for the users to read the manuscripts written by the author.

## Usage

The application is pretty straightforward to use, however, it is made of numerous interconnected components that focus on enriching user experience. This is a short summary of the main potential use cases:

1. **Library** - Library is an activity that fetches (from the internet) manuscripts from an API and displays *brief* information about the manuscript and its content. The fetched manuscripts, or so called entries, are filterable through: search, categorization, sorting
2. **Manuscript** - User can click on the entry card to navigate to the manuscript's specific page that holds even further information regarding the manuscript, most importantly the content of the manuscript. User can read the manuscript on the preferred font size in a minimalistic way and can also archive the manuscript for offline usage. Manuscript is either fetched from the API or loaded from the database depending on its state
3. **Archive** - Archive is an activity similar to the Library activity that displays the archived (locally archived/saved) list of manuscrips and it also features user friendly entry navigation filters: search, categorization, sorting
4. **Database** - Archiving feature is built around the local SQLite database that stores manuscripts which user deems to save. It has many useful methods, such as: getting, updating, reading and deleting
5. **About** - User can refer to the "About" menu item which creates a dialogue that holds information about the application itself and where the user can look into for further enrichment of knowledge
6. **H.P. Lovecraft** - User can click on this menu item to open a WebView client that takes the user to H. P. Lovecraft's Wikipedia page
7. **Settings** - Even though the application stores numerous types of user preferences through SharedPreferences, the most notable system setting is the Theme Mode, in which the user can choose which theme mode the application should use (**this can potentially alter user experience in a very significant way**): Light, Dark, Battery Saver, System Default
8. **The API and the fetched data** - It has to be noted that the application is built around the **custom** service implemented for this specific application. You can refer to the server and its documentation on another repository: https://github.com/GiorgiBeriashvili/lovecraft-api
9. **Miscellaneous** - The application has much more specific functionalities to offer for the user, which are mostly technical in a sense. You can explore all of that through the usage of the application itself or by browsing the source code

## Technical Details

Some parts of the application, especially database functionalities, are made asynchronous by utilizing Kotlin's coroutines.

The application sends GET requests via Retrofit to Lovecraft API service and sets the gathered data to the main page's recycler view, which in turn displays the launch information of all gathered items and enables the user to access specific manuscript information by clicking on the entry card, which in turn also sends a GET request to the desired manuscript endpoint.

Lovecraft API is decent in providing developers to customize their workflow based on their needs for using the service.

The rest of the application is built akin to lego blocks, by using different flavours of views that are available in the Android SDK and then logically coupling them together.

## Dependencies

The full list of dependencies are written in the **build.gradle** files.
