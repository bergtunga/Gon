Gon was originally meant to imitate this [gif](http://i.imgur.com/82FbFvi.gif)[(also available here)](http://imgfave.com/view/4403022), which I initially found on http://www.reddit.com/r/gonwild. One of my friends found the original artist on what I believe was Pinterest, where they had a variety of art but I lost the link. If you know the original artist, I would like to add a link to them.  
  
It's written to be work with a minimum of Java 6, but runs best with Java 7 x64.  
  
If you run into a problem with data caching, try running the program with more ram. How to do this depends on the operating system you're using, but if you're using windows, there is a .bat file available here which can be used to increase the ram available. For more info on this, lookup "Increasing Java VM heap size"  
  
You can edit the actual shape of the spinning thing using the left, middle, and right mouse buttons, in the bottom of the settings panel.  
**Left** will resize or rotate the shape / point. (Depending on if rotation, length, or both are selected)  
**Middle** will delete the point.  
**Right** will take the point out of the group, or if it's on a blank area will shift the positioning of the shape.  
The 'Sh[a]pe' button will let you add a regular polygon with a specified number of sides, a specified size, and a rotation between 0 and 2 x PI (6.28)  
	It will order the shape such that the new polygon's nodes will be after the final point.  
The 'Re[o]rder' button will order the points according to their rotation. It will not change the links.  
**Spacebar** will pause or resume the spinning of the shapes, and anything changed won't be updated, until the the application is resumed.  
  
**This project is no longer in development** If there is any interest, I can be persuaded to change the shape editor or refine the code comments. This was a highschool project for me, and I recognize the need for a refresh, if anyone is interested in it.  
  
*This is available under the [General Public Licence v3 (GPLv3), available here](http://www.gnu.org/licenses/gpl-3.0.html)*  
  
**Known issues:**  
Editor is not visually appealing, and the buttons get in the way.  
If the points are near the center, and decreased to a value of 0, the points get stuck, and cannot be pulled out.  
Switching between V-Ram and Ram does not properly recover ram, leading to excessive use of resources.  
  
**Planned Features:**  
Linking [points] in editor  
Single linked point editing [w/out adjusting rest of linked points]  
Changes in the way shapes are dragged and moved.  
Hiding the points, such that they can be re-enabled later, if the user wishes.  
More performance improvements  
  
**Changelog (1.4 + ) **  
1.5  
+ Added more comments & Refactored some source.  
+ Added moving of the shape in the shape editor, so it does not always have to be centered. This will be tweaked more later.  
+ Added center button.  
+ Split the DistanceMultiplier into 2 variables, a height and a width. NOTE: This, along with the colors is not version-stable. Expect changes.  
+ Added TileMethod, which is first step toward customized layouts / patterns of shapes.  
  
* Tweaked the results of clicking on the shape editor.  
* It now caches on startup.  
* Fixed secret.  
* Fixed bug that would cause excessive lag with some setups when the popup menu was opened.  
  
- Removed Optical Illusion preset, as it no longer works with new DistanceMultiplier values.  
  
1.45  
+ Added V-RAM caching  
  
* Changed to use V-RAM by default  
  
1.4  
+ Permits caching of shapes. Massive performance improvement at cost of memory usage.  
+ Permits AntiAliasing (It smooths the edges of the shapes) as an option. Significantly decreases performance, unless caching is enabled.  
+ Added an optical illusion preset. Works best when maximized.  
  
* Fixed adding things to the PopupMenu. (by opening saves)  
* Fixed direction of rotation compared to original image  
* Changed rotation to a full circle, from a third of a circle (Stages, Colour Passes made, rotationAmt, and Duration multiplied by 3)  
* Fixed distance delay to not depend on DistanceFactor or SizeFactor  