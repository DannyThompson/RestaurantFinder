# Restaurant Finder App
by Daniel Thompson

# Initial Setup
This app relies on Google Places, and thus needs a Google Places API key to access the Places and Maps APIs.
The API key is located within the .gitignored local.properties file, so if you wish to run this app, you will need to go
to your local.properties file, and add `GOOGLE_PLACES_API_KEY="<your_api_key_here>"`.
Once you rebuild the project, you should be able to compile and run it with no further issues.

You can get a free Places API key via the Google Developer console, and linking a billing account to your free created project.

# Description
Upon startup, this app will ask the user for location permissions.

Once location permissions are granted, the app will fetch a list of local restaurants using the user's
current Lat/Lng. On this list, the user can then tap on a restaurant to bring up a dialog with more details,
as well as a View Pager to view available photos.

From the List screen, the user may also favorite/unfavorite any restaurants they wish, and the status of these
favorites will persist across app sessions.

At the bottom of the screen, a floating action button is available to toggle between the list and the map screen.
On the map screen, the user will be able to view all of the location pins for all of the currently queried restaurants.

At the top of the screen is a search bar. The user may enter the name of a restaurant they wish to search for, and upon executing the search,
the list and map will be updated. A refresh button is available at the top right of the screen to refresh the current search and return the list and map
to showing all of the local restaurants, similar to first-load.

# Design/Architecture
This app uses an MVVM design pattern utilizing LiveData. The project is separated into modules:
1) API, which handles direct communication to the Api (in this case, maps.googleapis).
2) APP, which handles the UI.
3) CORE, which handles the models, DTOs, and other POJOs (POKotlinOs?).
4) SERVICE, which provides a layer of communication between the UI and API layers.

3rd party Libraries that are used are:
1) Dagger2 for dependency Injection
2) Coil for Image processing
3) OkHttp for Http client
4) RxJava for reactive/asynchronous programming
5) RxPermissions for permission checking built on top of RxJava.

# Screenshots
![image](https://user-images.githubusercontent.com/11155788/145342697-a7f89bcf-41bf-429e-a367-a29ccea2982c.png)

![image](https://user-images.githubusercontent.com/11155788/145342702-b0d6f7f4-6b7e-482e-84b0-fe22f25516de.png)

![image](https://user-images.githubusercontent.com/11155788/145342706-a1ec4ac8-bad1-49f5-9fb9-3c7a80f2dfee.png)

![image](https://user-images.githubusercontent.com/11155788/145342711-52a13cca-5e13-446f-88e8-33a011b54a65.png)

