#Design

###MVP
The minimum requirements for this app are:
- Selling and buying records. 
- The records as well as the offers should be distinguishable by id and data about the record. 
- The user should be able to create offers and complete ones on the marketplace. 
- Login and register should be done by firebase as well as the storage of user data and marketplace data. 
- The record data should be provided by lastfm's API. 
- The app should support landscape mode.

Optional items are:
- Wishlist
- Collection
- Navigation form collection to sell and wishlist to marketplace. 

###Activities
- Login
- Register
- Menu
- Profile
- Collection
- Wishlist
- Selling
- Buying 
- Record search
- Detail view (wishlist/collection/buying/selling)

A view of the flow:
![Login and start view](https://github.com/Bakenbraad/mprog_final/blob/master/doc/activity%20flow.PNG)

####Login
Allowing the user to authenticate using firebase. Directs through login to the menu, user can also navigate to registering.

####Register
Allows the user to register with an email, password and username.
Upon registering a user is directed to the login page.

####Menu
The main navigation pane is used for navigation to buy, sell, profile and logout (back to login).

####Profile
The profile leads to the collection and wishlist pages.

####Collection
The collection allows adding and removing of records, adding is done through searching (seperate activity) and selecting a record to view its details.
In these details the user can add the record to their collection. From the view of the collection the user may go directly to selling 
a selected record.

####Wishlist
The wishlist is similar to the collection page, the only difference is that from a records detailed view you can search the market for that record.

####Record search
This was mentioned for collection and wishlist as the activity that shows search results and when an item is clicked shows that records
data in the detail view.

####Detail view
This is where the actual adding can be done to the lists. This would display record details such as artist, title, maybe even tracks eventually.

####Selling
Selling can be done from the menu where it must pass the record search activity to select the record being sold. The user will then
be prompted for the price/bid/swap option and maybe a small story about this particular record (quality, year, condition).

####Buying
You can get to buying/the marketplace from the menu or the wishlist. Buying supports searching for records in its data and displays
basic data such as price, seller, title and year for example. The actual bidding can be done inside the corresponding detail view
which displays more detailed data.


###Classes
- User
- Record_sale
- Record

###Implementation of classes
It should be noted that this application uses firebase, the classes mentioned below are stored in firebase and are edited and retrieved by
a set of managers.
####User
The user class should contain two lists of ids, the first list contains
the ids of the users collection, these are mbids (music brainz, the music equivalent of imdb), as they just need to display record data, no selling
buying or other market data. Finally, the wishlist is quite identical to the collection but should be kept
seperately for seperate display. The user should also contain a username for others to view.

####Record_sale
A record has more info to it, however most can be obtained from the lastfm id. A tradeoff can be made here 
if its better to store data locally or to extract it from the lastfm API. Nevertheless, this class
should at least keep track of the title, artist, sale type, price, id and the user who is selling. The title and artist are 
pretty clear. These contain the record name and the band or artist that made it. The sale type should
be specified by the user upon creation of the object, this is when creating a market offer. The type
can be bidding, set price and even swapping. The price is then the lowest bid, a price or just plain swap.
On swap other users may offer records which the user will be personally be notified by (this implementation is
                                                                                             optional). Finally,
The user's uid should be added to the record as well to remember who is selling as well as for users to view their own offers.

####Record
This should only contain the music data and no market specifications. This would all be retrieved from the API.

###Managers
- Market manager
- Api manager
- Collection manager 
- Wishlist manager 
- Marketplace search manager
- Music Asynctask

###Implementation of managers
####Market manager
The market manager should create record objects and put them in the marketplace (firebase) with the appropriate info.

####Api manager
This manager requests data from the Lastfm api and returns an object containing just those that have a music brainz id (mbid), this is done
for practical storage and to prevent an album showing up in a multitude of formats. The api manager is also used for searching collection
and wishlist records.

####Collection and wishlist manager
This manager adds and removes ids from the users lists of collection and wishlist.

####Market search manager
The search manager should collaborate with the api manager in finding all corresponding mbids to the users query. When these are retrieved
the ids should be crossreferenced with the marketplace to give a viable result.

####Music Asynctask
Does the api background processing.

Interactions of the data structure:
An view of the flow:
![Login and start view](https://github.com/Bakenbraad/mprog_final/blob/master/doc/data%20structure.PNG)








