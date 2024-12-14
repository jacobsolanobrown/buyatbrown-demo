// import React, { useState } from "react";
// import axios from "axios";

// const AddListing = () => {
//   const [formData, setFormData] = useState({
//     uid: "",
//     username: "",
//     price: "",
//     title: "",
//     category: "",
//     tags: "",
//     condition: "",
//     description: "",
//     imageFile: null,
//   });

//   const [responseMessage, setResponseMessage] = useState("");
//   const [isSubmitting, setIsSubmitting] = useState(false);

//   const handleInputChange = (e) => {
//     const { name, value } = e.target;
//     setFormData({ ...formData, [name]: value });
//   };

//   const handleImageChange = (e) => {
//     const file = e.target.files[0];
//     setFormData({ ...formData, imageFile: file });
//   };

//   const handleSubmit = async (e) => {
//     e.preventDefault();
//     if (!formData.imageFile) {
//       setResponseMessage("Please upload an image.");
//       return;
//     }

//     setIsSubmitting(true);
//     setResponseMessage("");

//     try {
//       // Convert image to Base64
//       const reader = new FileReader();
//       reader.readAsDataURL(formData.imageFile);
//       reader.onload = async () => {
//         const base64Image = reader.result.split(",")[1]; // Strip out metadata

//         // Prepare form data
//         const data = {
//           ...formData,
//           imageUrl: base64Image,
//         };
        

//         try {
//           const response = await axios.post(
//             "http://localhost:3232/add-listings",
//             data.imageUrl, // Send the image URL as raw body
//             {
//               headers: { "Content-Type": "text/plain" },
//               params: {
//                 uid: data.uid,
//                 username: data.username,
//                 price: data.price,
//                 title: data.title,
//                 category: data.category,
//                 tags: data.tags,
//                 condition: data.condition,
//                 description: data.description,
//               },
//             }
//           );
//           setResponseMessage(
//             response.data.response_type === "success"
//               ? "Listing added successfully!"
//               : `Error: ${response.data.error}`
//           );
//         } catch (error) {
//           console.error("Error uploading listing:", error);
//           setResponseMessage("An error occurred while uploading the listing.");
//         }
//       };
//     } catch (error) {
//       console.error("Error uploading listing:", error);
//       setResponseMessage("An error occurred while uploading the listing.");
//     } finally {
//       setIsSubmitting(false);
//     }
//   };

//   return (
//     <div style={{ padding: "20px" }}>
//       <h2>Add a Listing</h2>
//       <form onSubmit={handleSubmit}>
//         <input
//           type="text"
//           name="uid"
//           placeholder="User ID"
//           value={formData.uid}
//           onChange={handleInputChange}
//           required
//         />
//         <br />
//         <input
//           type="text"
//           name="username"
//           placeholder="Username"
//           value={formData.username}
//           onChange={handleInputChange}
//           required
//         />
//         <br />
//         <input
//           type="number"
//           name="price"
//           placeholder="Price"
//           value={formData.price}
//           onChange={handleInputChange}
//           required
//         />
//         <br />
//         <input
//           type="text"
//           name="title"
//           placeholder="Title"
//           value={formData.title}
//           onChange={handleInputChange}
//           required
//         />
//         <br />
//         <input
//           type="text"
//           name="category"
//           placeholder="Category"
//           value={formData.category}
//           onChange={handleInputChange}
//           required
//         />
//         <br />
//         <input
//           type="text"
//           name="tags"
//           placeholder="Tags (comma-separated)"
//           value={formData.tags}
//           onChange={handleInputChange}
//           required
//         />
//         <br />
//         <select
//           name="condition"
//           value={formData.condition}
//           onChange={handleInputChange}
//           required
//         >
//           <option value="">Select Condition</option>
//           <option value="new">New</option>
//           <option value="like new">Like New</option>
//           <option value="used">Used</option>
//         </select>
//         <br />
//         <textarea
//           name="description"
//           placeholder="Description"
//           value={formData.description}
//           onChange={handleInputChange}
//           required
//         ></textarea>
//         <br />
//         <input
//           type="file"
//           accept="image/*"
//           onChange={handleImageChange}
//           required
//         />
//         <br />
//         <button type="submit" disabled={isSubmitting}>
//           {isSubmitting ? "Submitting..." : "Add Listing"}
//         </button>
//       </form>
//       {responseMessage && <p>{responseMessage}</p>}
//     </div>
//   );
// };

// export default AddListing;
