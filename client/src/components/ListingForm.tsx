import React, { useEffect, useState } from "react";
import { useUser } from "@clerk/clerk-react";
import { useNavigate } from "react-router-dom";
import axios from "axios";

interface PostingPageProps {
  uid: string;
  username: string;
}

const PostingPage: React.FC<PostingPageProps> = ({ uid, username }) => {
  const navigate = useNavigate();
  const [responseMessage, setResponseMessage] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  const [formData, setFormData] = useState({
    uid: "",
    title: "",
    price: "",
    username: "", // pass in the username (no need for user to type it in)
    description: "",
    condition: "",
    category: "",
    tags: "",
    imageFile: null,
    // imageFile: null,
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
    }
  };

  const goBack = () => {
    navigate("/");
  };

  return (
    <div className="flex flex-col align-center min-h-screen bg-gradient-to-r from-blue-100 to-pink-100">
      <div className="min-w-2xl mx-12 my-14 p-8 rounded-3xl shadow-lg  bg-white/50 ">
        <button className="py-4 px-2 rounded-md underline" onClick={goBack}>
          Back to Listings
        </button>
        <h1 className="text-3xl font-bold mb-6">Post a New Listing</h1>
        <h1>{uid}</h1>
        <h1>{username}</h1>
        <form onSubmit={handleSubmit} className="space-y-4">
          {/* Input for UID: REMOVE LATER */}
          <div>
            <label
              htmlFor="uid"
              className="block text-sm font-medium text-gray-700"
            >
              UID:
            </label>
            <input
              type="uid"
              id="price"
              name="uid"
              placeholder="Enter your uid"
              value={formData.uid}
              onChange={handleChange}
              className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm p-2"
              required
            />
          </div>
          {/* Input for USERNAME: REMOVE LATER */}
          <div>
            <label
              htmlFor="username"
              className="block text-sm font-medium text-gray-700"
            >
              Username:
            </label>
            <input
              type="username"
              id="price"
              name="username"
              placeholder="Enter your username"
              value={formData.username}
              onChange={handleChange}
              className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm p-2"
              required
            />
          </div>
          {/* Input for a listing Title */}
          <div>
            <label
              htmlFor="title"
              className="block text-sm font-medium text-gray-700"
            >
              Title:
            </label>
            <input
              type="text"
              id="title"
              name="title"
              placeholder="Choose a listing title"
              value={formData.title}
              onChange={handleChange}
              className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm p-2"
              required
            />
          </div>
          {/* Input for price */}
          <div>
            <label
              htmlFor="price"
              className="block text-sm font-medium text-gray-700"
            >
              Price:
            </label>
            <input
              type="text"
              id="price"
              name="price"
              placeholder="Choose a price"
              value={formData.price}
              onChange={handleChange}
              className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm p-2"
              required
            />
          </div>
          {/* Input for imageUrl */}
          <div>
            <label
              htmlFor="imageUrl"
              className="block text-sm font-medium text-gray-700"
            >
              Photo:
            </label>
            <input
              type="file"
              accept="image/png, image/jpeg"
              id="imageUrl"
              name="imageUrl"
              placeholder="Upload an image (png or jpeg)"
              onChange={handleImageChange}
              className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm p-2"
              required
            />
          </div>
          {/* Input for a description */}
          <div>
            <label
              htmlFor="description"
              className="block text-sm font-medium text-gray-700"
            >
              Description:
            </label>
            <textarea
              id="description"
              name="description"
              placeholder="Describe your listing"
              value={formData.description}
              onChange={handleChange}
              className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm p-2"
              required
            />
          </div>
          {/* Dropdown menu to choose item condition */}
          <div>
            <label
              htmlFor="condition"
              className="block text-sm font-medium text-gray-700"
            >
              Condition
            </label>
            <input
              type="text"
              id="condition"
              name="condition"
              placeholder="Choose a condition"
              value={formData.condition}
              onChange={handleChange}
              className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm p-2"
              required
            />
          </div>
          {/* Dropdown menu to choose item category */}
          <div>
            <label
              htmlFor="category"
              className="block text-sm font-medium text-gray-700"
            >
              Category
            </label>
            <input
              type="text"
              id="category"
              name="category"
              placeholder="Choose a category"
              value={formData.category}
              onChange={handleChange}
              className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm p-2"
              required
            />
          </div>
          {/* Add tags */}
          <div>
            <label
              htmlFor="tags"
              className="block text-sm font-medium text-gray-700"
            >
              Tags
            </label>
            <input
              type="text"
              id="tags"
              name="tags"
              placeholder="Add tags"
              value={formData.tags}
              onChange={handleChange}
              className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm p-2"
              required
            />
          </div>
          {/* Submit the listing (Call the Create Listing API endpoint) */}
          <button
            type="submit"
            className="w-full bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700"
          >
            Post Listing
          </button>
        </form>
      </div>
    </div>
  );
};

export default PostingPage;
