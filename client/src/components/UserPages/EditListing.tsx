// import React, { useState, useEffect } from "react";
// import { useNavigate } from "react-router-dom";
// import axios from "axios";

// const EditListingPage: React.FC = () => {
//   const navigate = useNavigate();
//   const [responseMessage, setResponseMessage] = useState("");
//   const [isSubmitting, setIsSubmitting] = useState(false);
//   const [formData, setFormData] = useState({
//     uid: "",
//     listingId: "",
//     title: "",
//     price: "",
//     description: "",
//     condition: "",
//     category: "",
//     tags: "",
//   });

//   useEffect(() => {
//     // Fetch existing listing details
//     const params = new URLSearchParams(window.location.search);
//     const uid = params.get("uid") || "";
//     const listingId = params.get("listingId") || "";

//     setFormData((prevData) => ({
//       ...prevData,
//       uid,
//       listingId,
//     }));

//     if (uid && listingId) {
//       axios
//         .get(`http://localhost:3232/get-listing`, {
//           params: { uid, listingId },
//         })
//         .then((response) => {
//           const data = response.data;
//           setFormData((prevData) => ({
//             ...prevData,
//             title: data.title || "",
//             price: data.price || "",
//             description: data.description || "",
//             condition: data.condition || "",
//             category: data.category || "",
//             tags: data.tags || "",
//           }));
//         })
//         .catch((error) => {
//           console.error("Error fetching listing details:", error);
//           setResponseMessage("Error fetching listing details.");
//         });
//     }
//   }, []);

//   const handleChange = (
//     e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
//   ) => {
//     const { name, value } = e.target;
//     setFormData((prevData) => ({
//       ...prevData,
//       [name]: value,
//     }));
//   };

//   const handleSubmit = (e: React.FormEvent) => {
//     e.preventDefault();

//     setIsSubmitting(true);
//     setResponseMessage("");

//     axios
//       .post(
//         `http://localhost:3232/update-listings`,
//         {},
//         {
//           params: {
//             uid: formData.uid,
//             listingId: formData.listingId,
//             title: formData.title,
//             price: formData.price,
//             description: formData.description,
//             condition: formData.condition,
//             category: formData.category,
//             tags: formData.tags,
//           },
//         }
//       )
//       .then((response) => {
//         setResponseMessage(
//           response.data.response_type === "success"
//             ? "Listing updated successfully!"
//             : `Error: ${response.data.error}`
//         );
//       })
//       .catch((error) => {
//         console.error("Error updating listing:", error);
//         setResponseMessage("An error occurred while updating the listing.");
//       })
//       .finally(() => {
//         setIsSubmitting(false);
//       });
//   };

//   const goBack = () => {
//     navigate("/");
//   };

//   return (
//     <div className="flex flex-col align-center min-h-screen bg-gradient-to-r from-blue-100 to-pink-100">
//       <div className="min-w-2xl mx-12 my-14 p-8 rounded-3xl shadow-lg bg-white/50">
//         <button className="py-4 px-2 rounded-md underline" onClick={goBack}>
//           Back to Listings
//         </button>
//         <h1 className="text-3xl font-bold mb-6">Edit Listing</h1>
//         <form onSubmit={handleSubmit} className="space-y-4">
//           {/* Input for Title */}
//           <div>
//             <label
//               htmlFor="title"
//               className="block text-sm font-medium text-gray-700"
//             >
//               Title:
//             </label>
//             <input
//               type="text"
//               id="title"
//               name="title"
//               placeholder="Edit listing title"
//               value={formData.title}
//               onChange={handleChange}
//               className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm p-2"
//             />
//           </div>
//           {/* Input for Price */}
//           <div>
//             <label
//               htmlFor="price"
//               className="block text-sm font-medium text-gray-700"
//             >
//               Price:
//             </label>
//             <input
//               type="text"
//               id="price"
//               name="price"
//               placeholder="Edit price"
//               value={formData.price}
//               onChange={handleChange}
//               className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm p-2"
//             />
//           </div>
//           {/* Input for Description */}
//           <div>
//             <label
//               htmlFor="description"
//               className="block text-sm font-medium text-gray-700"
//             >
//               Description:
//             </label>
//             <textarea
//               id="description"
//               name="description"
//               placeholder="Edit description"
//               value={formData.description}
//               onChange={handleChange}
//               className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm p-2"
//             />
//           </div>
//           {/* Input for Condition */}
//           <div>
//             <label
//               htmlFor="condition"
//               className="block text-sm font-medium text-gray-700"
//             >
//               Condition:
//             </label>
//             <input
//               type="text"
//               id="condition"
//               name="condition"
//               placeholder="Edit condition"
//               value={formData.condition}
//               onChange={handleChange}
//               className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm p-2"
//             />
//           </div>
//           {/* Input for Category */}
//           <div>
//             <label
//               htmlFor="category"
//               className="block text-sm font-medium text-gray-700"
//             >
//               Category:
//             </label>
//             <input
//               type="text"
//               id="category"
//               name="category"
//               placeholder="Edit category"
//               value={formData.category}
//               onChange={handleChange}
//               className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm p-2"
//             />
//           </div>
//           {/* Input for Tags */}
//           <div>
//             <label
//               htmlFor="tags"
//               className="block text-sm font-medium text-gray-700"
//             >
//               Tags:
//             </label>
//             <input
//               type="text"
//               id="tags"
//               name="tags"
//               placeholder="Edit tags"
//               value={formData.tags}
//               onChange={handleChange}
//               className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm p-2"
//             />
//           </div>
//           {/* Submit the edits */}
//           <button
//             type="submit"
//             className="w-full bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700"
//             disabled={isSubmitting}
//           >
//             Save Changes
//           </button>
//         </form>
//         {responseMessage && <p className="mt-4 text-center">{responseMessage}</p>}
//       </div>
//     </div>
//   );
// };

// export default EditListingPage;
