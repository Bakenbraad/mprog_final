## Week 1
### Monday
Wrote proposal, created basic design for main activities. Description of basic flow of activities and code design. Can be found in README.md.
### Tuesday
Wrote design in detail. This is found in DESIGN.md, the MVP is specified here, all activities managers and classes are explained thouroughly.
The file is accompanied by a flow diagram to get a picture of the activities and how to navigate between them. Another diagram
shows how different sections influence each other and how managers maintain the data in each section. 
### Wednesday
The proposal should be edited slightly to match developments as a result of the design stage. Some extra activities are needed for the app
but won't affect the overall functionality, merely optimize it. The project is created and added to github and firebase.
The classes for users and records are created and firebase is implemented in the gradle files as well as the google-services file.
### Thursday
Not much progress on the technical part, stuck on API processing, main files for the design are created and set up. Asynctask was chosen for searching for records.
### Friday - Sunday
Api manager can now parse requests into a listview with the images for records being seperately loaded. Layout has been boosted a lot.
## Week 2
### Monday
Authentication is being buggy took a whole day to fix internal errors, however a large bug remains. The google play version is not updatable yet, project may have to be downgraded as a whole (Postponed).
### Tuesday
The authentication works with unique usernames, email, email verivication and all toasts/error pop ups are in place for the user to properly register an account. From the search results you can now create an offer with a nice layout, this is not yet written to firebase.
### Wednesday
The entire project had to be downgraded in order for firebase to work. This took a while, however everything seems to work fine now and navigation has been moved to a drawer object. The offers created are now written to the firebase.
### Thursday
The user is finally properly welcomed in the main activity. The offers can now also be queried in the marketplace in correspondence to the original api query. The offers are displayed in a nice layout through an adapter.
## Week 3
### Monday
Worked on layout and presenting marketsearch data in a new activity (so the user is able to respond to presented offers).
### Tuesday
Worked even more on layout, started implementing a message system so users can contact eachother if they respond to an offer.
The message system operates with automatically formatted messages to prevent misuse, only when users agree to trade/sell a record can
they view each others email for further contact.
### Wednesday
A long day of perfecting much of the message system, with dialogs, accept/reject system for offers, preventing users from misusing the
message system in many ways (no invalid offers/ repeated offers) and removing completed offers from the marketplace. The login/register
layout was also redone.
Activity and resource names have been reviewed in order to maintain a clear structure of the project.
### Thursday
Perfected messaging with replies and read/unread status. Fixed navigation, created profile/profile updates, implemented wishlist and collections. These can be expanded.
Cleaned up a load of code. Renamed many classes to create a uniform format.
## Week 4
### Monday
Done loads of commenting, fixed minor layout bugs, smoothend ui. Home screen is now a lot easier on the eyes. Code is being refractored in
order to pass bettercodehub.
### Tuesday
More refractoring, made the app phone accessible. The messagesystem was tweaked to better show read/unread messages. Info screen added to
main activity for new users. Profile now also shows users sales. You can also navigate from collection to selling and wishlist to market.
### Wednesday 
More layout fixes, loads and loads of commenting. Extensive bug testing.
### Thursday
Final commenting, last bug fixes and more code cleaning.

