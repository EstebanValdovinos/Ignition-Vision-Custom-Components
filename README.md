# Vision Component Example

⚙️ Ignition Vision Custom Components

A collection of modern UI components for Ignition Vision built using the Ignition SDK and Java Swing.

This module provides clean, interactive controls inspired by modern mobile interfaces, designed specifically for industrial HMIs and SCADA systems.

The objective of this project is to provide more intuitive operator interfaces while maintaining full compatibility with Ignition Vision bindings, scripting, and property systems.

✨ Features

✔ Custom Vision components written in Java
✔ Fully compatible with Ignition property bindings
✔ Smooth animations and modern UI design
✔ Built using the Ignition SDK module framework
✔ Easily extendable for future components

🧩 Components Included

The module currently includes four custom Vision components:

Component	Description
🔘 iOS Toggle	Modern animated toggle switch
🟦 iOS Button	Clean mobile-style button
🟢 Status Indicator	Animated system status indicator
👉 Slide To Confirm	Drag-based confirmation control

All components appear in the Vision Designer under:

📦 Component Palette → Custom Components

🔘 iOS Toggle

A modern toggle switch inspired by the iOS switch control, designed for binary states such as On/Off, Enable/Disable, Start/Stop, etc.

✨ Features

Smooth animated toggle transition

Customizable colors

Fully bindable selected property

Mouse and keyboard interaction support

Disabled state visual handling

⚙ Key Properties
Property	Description
selected	Boolean state of the switch
trackOnColor	Background color when the toggle is ON
trackOffColor	Background color when the toggle is OFF
enabled	Enables or disables user interaction
🏭 Typical Use Cases

Machine start / stop

System enable / disable

Mode switching

Feature toggles

🟦 iOS Button

A modern button styled after mobile UI controls, providing a clean and minimal visual design while still functioning like a traditional Vision button.

✨ Features

Adjustable corner radius

Customizable colors

Configurable font and text styling

Fully bindable properties

Clean and modern appearance

⚙ Key Properties
Property	Description
text	Button label
background	Button background color
foreground	Text color
cornerRadius	Radius used to round button corners
enabled	Enables or disables interaction
🏭 Typical Use Cases

Operator commands

Navigation buttons

Action triggers

Manual control actions

🟢 Status Indicator

A visual component used to display equipment or process status with animated feedback.

The indicator provides clear visual feedback and can optionally animate to attract operator attention.

✨ Features

Pulse animation mode

Blink animation mode

Customizable ON/OFF colors

Configurable animation speed

Optional label positioning

⚙ Key Properties
Property	Description
onColor	Indicator color when active
offColor	Indicator color when inactive
animationMode	Pulse or blink mode
animationSpeed	Speed of the animation
textPosition	Location of the label relative to the indicator
🏭 Typical Use Cases

Equipment status

Alarm indicators

Process state display

System health monitoring

👉 Slide To Confirm

A safety-oriented control that requires the operator to slide a knob across a track before triggering an action.

This pattern helps prevent accidental presses, making it ideal for critical operations.

✨ Features

Drag-to-confirm interaction

Configurable confirmation threshold

Pulse feedback when the threshold is reached

Optional automatic reset

Customizable colors and icons

Smooth animation effects

⚙ Key Properties
Property	Description
text	Default message displayed on the control
activeText	Text displayed when confirmed
iconPath	Optional path to a custom icon
confirmThreshold	Minimum slide percentage required to confirm
autoReset	Automatically reset after confirmation
resetDelay	Delay before resetting the control
pulseColor	Color used for the confirmation pulse animation
🏭 Typical Use Cases

Emergency stop confirmation

Machine reset

Batch start confirmation

Safety-critical commands

Operator acknowledgment actions

📦 Installation
1️⃣ Build the module using Maven
mvn clean install
2️⃣ Locate the generated module file
ce-build/target/*.modl
3️⃣ Install the module in the Ignition Gateway
Config → Modules → Install or Upgrade
4️⃣ Restart the Ignition Designer

The components will appear in the palette under:

Custom Components
🗂 Project Structure

This project follows the Ignition SDK example module layout.

ce-client
Vision component implementations

ce-designer
Designer palette integration and BeanInfo classes

ce-build
Module assembly
📋 Requirements

Ignition 8.x

Java 11+

Maven

🚀 Future Plans

Additional components planned for future versions:

Animated progress indicators

Modern alarm indicators

Advanced toggle groups

Slider controls

Additional HMI-optimized input widgets

👨‍💻 Author

Esteban
Controls Engineer | SCADA Developer