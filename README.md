# Abiti
<p>JAVA - ANDROID APPLICATION</p>
<p>Firebase - NoSQL DATABASE</p>

<p>App summary: users send two images with a description so other users can compare which image they like the most, either the user that sent and the users that vote are anonymous.</p>
<p>Users can log in using Firebase authentication. The images are stored at Firebase Storage. Publication information, like image download URL and description, is stored at a NoSQL database (Cloud Firestore).</p>
<p>Keynote:</p>
<ul>
    <li>Images are loaded with Glide</li>
    <li>Scrolling feed is populated via RecyclerView</li>
    <li>Utility classes to be reused, like RecyclerGridFragment: the activity sends the path of the database to populate the RecyclerView and can choose if it will be a grid view or a linear view, so it can be reused along other functionalites at the app</li>
</ul>
<p>Functions still not implemented:</p>
<ul>
    <li>"Settings" Activity so user can choose which hashtags they are interest to see at main feed.</li>
    <li>Search tool, so user can search a tag they want.</li>
    <li>Voting and comments at each publication.</li>
    <li>Camera so user can send an image right away</li>
</ul>
<p>Main feed</p>
<img src="https://user-images.githubusercontent.com/38297512/41006831-2061da46-68f1-11e8-8270-987e7c0516a9.png" width = "150">
<p>New publication</p>
<p>
<img src="https://user-images.githubusercontent.com/38297512/41007044-ff9d299a-68f1-11e8-8239-3f117f08bdb0.png" width = "150"> <img src="https://user-images.githubusercontent.com/38297512/41007047-0225ffb6-68f2-11e8-9045-e77f6f183c63.png" width = "150">
</p>
<p>Single publication</p>
<img src="https://user-images.githubusercontent.com/38297512/41007040-fbf36e1c-68f1-11e8-9c63-aa9f57ad00fb.png" width = "150">

