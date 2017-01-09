# Proposal
### By Rens van der Veldt

#### Problem
The biggest problem for many people that collect vinyl records is that they have many records that they want and can't find. Yet they also have double records that they obviously don't need. The solution is buying and selling in a likeminded community! However when you search for vinyl on sites like marktplaats.nl (A large dutch public market place site) you only find a couple of offers and many are shops that are advertising. The alternative would be discogs.com, a site that allows users to trade music related goods (primarily records), however the app they provide is very user unfriendly and basicly shows the website in app form.

My app is going to create a user friendly vinyl collection, trade and market experience, with last.fm's api to request record data and present it in a clean format. Users can buy, sell and (optionally) swap records through instinctive interfaces. User data, a wantlist and a collection list, will be kept in firebase.

#### Solution

The login screen should be simple with options to log in and/or create an account.
The main menu will be more interesting with a clean 5 button interface, a buy, sell, profile and settings button presented in a window and below a logout button to return to the login activity.

![Login and start view](https://github.com/Bakenbraad/mprog_final/blob/master/doc/login%20and%20start.png)

The actual marketplace should show a list of recent offers by default, users can search for certain albums here after which only those being sold are shown, togeather with the amount being sold, as well as the price/record requested. The marketplace should support 3 methods of payment, price, bidding and trade. The list items should display all this togeather with basic info about the record, artist, year, title and cover.

![Marketplace](https://github.com/Bakenbraad/mprog_final/blob/master/doc/marketplace.png)
![Item](https://github.com/Bakenbraad/mprog_final/blob/master/doc/listalbum.png)

Selling should be similar and allow the user to search and add records to their selling list (which is part of the user profile). Upon searching, a list should be displayed with in stead of price data, an add button. This allows the user to specify their offer in a seperate activity.

![Selling](https://github.com/Bakenbraad/mprog_final/blob/master/doc/Sell%20screen.png)

Profile should consist of 3 seperate pages, user data, wishlist and collection. The collection is a simple listview of what you have, a similar implementation is found in the discogs app. The wishlist should be essentially the same but needs some way to distinguish from the collection. Both should support searching and adding/removing. The wishlist might show if offers for certain records are present.
The user data contains basic info about the user, nickname, location, age, email etc. Optional for the user interface is changing passwords/email. The wishlist and collection are semi-optional and should be implemented after the basic operability of the app (namely buying and selling) is finished.

Finally, settings should be implemented in a simple way. Examples are push notifications, sounds, emails and many optional settings which will be determined later depending on the layout and functionality of the app.
