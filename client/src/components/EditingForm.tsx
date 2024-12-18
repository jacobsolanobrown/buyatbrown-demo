import React, { useEffect, useState } from "react";
import { useUser } from "@clerk/clerk-react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { P } from "@clerk/clerk-react/dist/useAuth-D1ySo1Ar";

interface EditingPageProps {
  uid: string;
  listing?: {
    imageUrl: string;
    title: string;
    price: string;
    username: string;
    description: string;
    condition: string;
    category: string;
    tags: string;
    listingId: string;
  };
}

const EditingPage: React.FC<EditingPageProps> = ({ 
    uid, 
    listing,
}) => {
  const navigate = useNavigate();
  const [responseMessage, setResponseMessage] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  const [formData, setFormData] = useState({
    uid: uid || "",
    title: listing?.title || "",
    price: listing?.price || "",
    username: listing?.username || "",
    description: listing?.description || "",
    condition: listing?.condition || "",
    category: listing?.category || "",
    tags: listing?.tags || "",
    imageFile: null,
  });

  // Handle form input changes
  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    const { name, value } = e.target;
    setFormData((prevData) => ({
      ...prevData,
      [name]: value,
    }));
  };

  // Handle form image changes
  const handleImageChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    const file = e.target.files[0];
    setFormData({ ...formData, imageFile: file });
  };

  /**
 * This handles form submission, validating all of the inputs and whether we can post a listing. It handles posting an image to our 
 * separate logic for i,mage posting.
 * 
 * @param e The event when clicking the submit button on the form 
 * @returns On success, it should return the user back to the home screen 
 * // TODO: add a success screen for posting and allow the user to navigate to their listings 

 */
  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    // Must upload an image
    if (!formData.imageFile) {
      setResponseMessage("Please upload an image.");
      return;
    }

    setIsSubmitting(true);
    setResponseMessage("");

    try {
      // Convert image to Base64
      const reader = new FileReader();
      reader.readAsDataURL(formData.imageFile);
      reader.onload = async () => {
        // Error check the reader result to prevent null pointer exception
        const base64Image = reader.result ? reader.result.split(",")[1] : ""; // Strip out metadata

        // Prepare form data
        const data = {
          ...formData,
          imageUrl: base64Image,
        };

        try {
          const response = await axios.post(
            "http://localhost:3232/add-listings",
            data.imageUrl, // Send the image URL as raw body
            {
              headers: { "Content-Type": "text/plain" },
              params: {
                username: data.username,
                price: data.price,
                title: data.title,
                category: data.category,
                tags: data.tags,
                condition: data.condition,
                description: data.description,
              },
            }
          );
          setResponseMessage(
            response.data.response_type === "success"
              ? "Listing added successfully!"
              : `Error: ${response.data.error}`
          );
        } catch (error) {
          console.error("Error uploading listing:", error);
          setResponseMessage("An error occurred while uploading the listing.");
        }
      };
    } catch (error) {
      console.error("Error uploading listing:", error);
      setResponseMessage("An error occurred while uploading the listing.");
    } finally {
      setIsSubmitting(false);
    }

    // Handle form submission logic here
    console.log("Form data submitted:", formData);
    if (!formData.imageFile) {
      setResponseMessage("Please upload an image.");
      return;
    }

    setIsSubmitting(true);
    setResponseMessage("");

    try {
      // Convert image to Base64
      const reader = new FileReader();
      reader.readAsDataURL(formData.imageFile);
      reader.onload = async () => {
        // Error check the reader result to prevent null pointer exception
        const base64Image = reader.result ? reader.result.split(",")[1] : ""; // Strip out metadata

        // Prepare form data
        const data = {
          ...formData,
          imageUrl: base64Image,
        };

        try {
          const response = await axios.post(
            "http://localhost:3232/add-listings",
            data.imageUrl, // Send the image URL as raw body
            {
              headers: { "Content-Type": "text/plain" },
              params: {
                uid: data.uid,
                username: data.username,
                price: data.price,
                title: data.title,
                category: data.category,
                tags: data.tags,
                condition: data.condition,
                description: data.description,
              },
            }
          );
          setResponseMessage(
            response.data.response_type === "success"
              ? "Listing added successfully!"
              : `Error: ${response.data.error}`
          );
        } catch (error) {
          console.error("Error uploading listing:", error);
          setResponseMessage("An error occurred while uploading the listing.");
        }
      };
    } catch (error) {
      console.error("Error uploading listing:", error);
      setResponseMessage("An error occurred while uploading the listing.");
    } finally {
      setIsSubmitting(false);
      navigate("/");
    }
  };

  const goBack = () => {
    navigate("/yourlistings");
  };

  return (
    <div className="flex flex-col align-center items-center min-h-screen bg-gradient-to-r from-blue-100 to-pink-100">
      <p>{uid}</p>

      <div className="w-3/4 mx-12 my-14 p-8 rounded-3xl shadow-lg  bg-white/50 ">
        <button className="py-4 px-2 underline" onClick={goBack}>
          Cancel
        </button>
        <h1 className="text-3xl font-bold mb-6 ml-2">Edit Your Listing</h1>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label
              htmlFor="title"
              className="block text-xl font-medium text-gray-700 ml-3"
            >
              Listing Title:
            </label>
            <input
              type="text"
              id="Listing Title"
              name="title"
              placeholder={listing?.title || "Enter title"}
              value={formData.title}
              onChange={handleChange}
              className="mt-2 block w-full border border-gray-300 rounded-full shadow-sm px-4 py-2"
              required
            />
          </div>
          {/* Input for price */}
          <div>
            <label
              htmlFor="price"
              className="block text-xl font-medium text-gray-700 ml-3"
            >
              Price
            </label>
            <input
              type="text"
              id="price"
              name="price"
              placeholder={listing?.price || "Enter price"}
              value={formData.price}
              onChange={handleChange}
              className="mt-2 block w-full border border-gray-300 rounded-full shadow-sm px-4 py-2"
              required
            />
          </div>
          {/* Input for imageUrl */}
          <div>
            <label
              htmlFor="imageUrl"
              className="block text-xl font-medium text-gray-700 ml-3"
            >
              Photo
            </label>
            <input
              type="file"
              accept="image/png, image/jpeg"
              id="imageUrl"
              name="imageUrl"
              placeholder="Upload an image (png or jpeg)"
              onChange={handleImageChange}
              className="mt-2 block w-full border-gray-300  px-2 py-2 rounded-full"
              required
            />
          </div>
          {/* Input for a description */}
          <div>
            <label
              htmlFor="description"
              className="block text-xl font-medium text-gray-700 ml-3"
            >
              Description
            </label>
            <div>
              <textarea
                id="description"
                name="description"
                placeholder={listing?.description || "Enter description"}
                value={formData.description}
                onChange={handleChange}
                className=" block w-full border border-gray-300 rounded-xl shadow-sm px-4 py-2"
                required
              />
            </div>
          </div>
          {/* Dropdown menu to choose item condition */}
          <div>
            <label
              htmlFor="condition"
              className="block text-xl font-medium text-gray-700 ml-3"
            >
              Condition
            </label>
            <input
              type="text"
              id="condition"
              name="condition"
              placeholder={listing?.condition || "Enter condition"}
              value={formData.condition}
              onChange={handleChange}
              className="mt-2 block w-full border border-gray-300 rounded-full shadow-sm px-4 py-2"
              required
            />
          </div>
          {/* Dropdown menu to choose item category */}
          <div>
            <label
              htmlFor="category"
              className="block text-xl font-medium text-gray-700 ml-3"
            >
              Category
            </label>
            <input
              type="text"
              id="category"
              name="category"
              placeholder={listing?.category || "Enter category"}
              value={formData.category}
              onChange={handleChange}
              className="mt-2 block w-full border border-gray-300 rounded-full shadow-sm px-4 py-2 "
              required
            />
          </div>
          {/* Add tags */}
          <div>
            <label
              htmlFor="tags"
              className="block text-xl font-medium text-gray-700 ml-3"
            >
              Tags
            </label>
            <input
              type="text"
              id="tags"
              name="tags"
              placeholder={listing?.tags || "Enter tags"}
              value={formData.tags}
              onChange={handleChange}
              className="mt-2 block w-full border border-gray-300 rounded-full shadow-sm px-4 py-2"
              required
            />
          </div>
          {/* Submit the listing (Call the Create Listing API endpoint) */}
          <button
            type="submit"
            className="w-full bg-blue-600 text-white py-2 px-4 rounded-full hover:bg-blue-700 text-xl font-bold"
          >
            Post Listing
          </button>
        </form>
      </div>
    </div>
  );
};

export default EditingPage;
